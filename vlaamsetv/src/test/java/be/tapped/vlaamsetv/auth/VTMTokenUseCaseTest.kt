package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.auth.prefs.vtm.VTMTokenStore
import be.tapped.vlaamsetv.credentialsArb
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

class VTMTokenUseCaseTest : BehaviorSpec({
    given("A ${VTMTokenUseCase::class.java.simpleName}") {
        val profileRepo = mockk<AuthenticationRepo>()
        val vtmTokenStore = mockk<VTMTokenStore>()
        val vrtErrorMessageConverter = mockk<ErrorMessageConverter<ApiResponse.Failure>>()

        val sut = VTMTokenUseCase(profileRepo, vtmTokenStore, vrtErrorMessageConverter)

        val username = Arb.string().gen()
        val password = Arb.string().gen()
        `when`("logging in") {
            and("it was successful") {
                val token = vtmTokenWrapperArb.gen()
                coEvery {
                    profileRepo.login(
                        username,
                        password
                    )
                } returns ApiResponse.Success.Authentication.Token(token).right()

                sut.performLogin(username, password)

                then("it should save the credentials") {
                    coVerify { vtmTokenStore.saveVTMCredentials(username, password) }
                }

                then("it should save the token") {
                    coVerify { vtmTokenStore.saveToken(token) }
                }
            }

            and("it was not successful") {
                coEvery {
                    profileRepo.login(
                        username,
                        password
                    )
                } returns ApiResponse.Failure.EmptyJson.left()

                val result = sut.performLogin(username, password)

                then("it should return the failure") {
                    result shouldBe ApiResponse.Failure.EmptyJson.left()
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
                    profileRepo.login(
                        credential.username,
                        credential.password
                    )
                } returns ApiResponse.Success.Authentication.Token(vtmTokenWrapperArb.gen()).right()

                val result = sut.refresh()

                then("it should return true") {
                    result shouldBe true.right()
                }
            }
        }
    }
})
