package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.auth.prefs.vtm.VTMTokenStore
import be.tapped.vlaamsetv.credentialsArb
import be.tapped.vlaamsetv.errorMessageArb
import be.tapped.vlaamsetv.gen
import be.tapped.vlaamsetv.vtmTokenWrapperArb
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.profile.AuthenticationRepo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify

class VTMTokenUseCaseTest : BehaviorSpec({
    given("A ${VTMTokenUseCase::class.java.simpleName}") {
        val profileRepo = mockk<AuthenticationRepo>()
        val vtmTokenStore = mockk<VTMTokenStore>()
        val vtmErrorMessageConverter =
            mockk<ErrorMessageConverter<ApiResponse.Failure>>()
        val tokenRefreshWorkerScheduler = mockk<TokenRefreshWorkScheduler>()

        val sut = VTMTokenUseCase(profileRepo,
            vtmTokenStore,
            vtmErrorMessageConverter,
            tokenRefreshWorkerScheduler)

        val stringGen = Arb.string(minSize = 1)
        val username = stringGen.gen()
        val password = stringGen.gen()
        `when`("logging in") {
            and("provided empty credentials") {
                val result = sut.performLogin("", "")

                then("it should return with an error message") {
                    result shouldBe ErrorMessage(R.string.failure_generic_no_email).left()
                }

                then("it should not make a call") {
                    coVerify(exactly = 0) { profileRepo.login("", "") }
                }
            }

            and("it was successful") {
                val token = vtmTokenWrapperArb.gen()
                coEvery {
                    profileRepo.login(username, password)
                } returns ApiResponse.Success.Authentication.Token(token).right()

                sut.performLogin(username, password)

                then("it should save the credentials") {
                    coVerify { vtmTokenStore.saveVTMCredentials(username, password) }
                }

                then("it should save the token") {
                    coVerify { vtmTokenStore.saveToken(token) }
                }

                then("it should schedule the background token refresh") {
                    verify { tokenRefreshWorkerScheduler.scheduleTokenRefreshVTM() }
                }
            }

            and("it was not successful") {
                coEvery {
                    profileRepo.login(username, password)
                } returns ApiResponse.Failure.EmptyJson.left()

                val errorMessage = errorMessageArb.gen()
                coEvery { vtmErrorMessageConverter.mapToHumanReadableError(ApiResponse.Failure.EmptyJson) } returns errorMessage

                val result = sut.performLogin(username, password)

                then("it should return the failure") {
                    result shouldBe errorMessage.left()
                }
            }
        }

        `when`("refreshing") {
            and("it does not have any credentials stored") {
                coEvery { vtmTokenStore.vtmCredentials() } returns null

                val result = sut.refresh()

                then("it should return false") {
                    result shouldBe false.right()
                }
            }

            and("it has credentials stored") {
                val credential = credentialsArb.gen()
                coEvery { vtmTokenStore.vtmCredentials() } returns credential
                coEvery {
                    profileRepo.login(credential.username, credential.password)
                } returns ApiResponse.Success.Authentication
                    .Token(vtmTokenWrapperArb.gen())
                    .right()

                val result = sut.refresh()

                then("it should return true") {
                    result shouldBe true.right()
                }
            }
        }
    }
})
