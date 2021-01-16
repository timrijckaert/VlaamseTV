package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vier.ApiResponse
import be.tapped.vier.profile.ProfileRepo
import be.tapped.vlaamsetv.*
import be.tapped.vlaamsetv.auth.prefs.vier.VIERTokenStore
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify

class VIERTokenUseCaseTest : BehaviorSpec({
    given("A ${VIERTokenUseCase::class.simpleName}") {
        val profileRepo = mockk<ProfileRepo>()
        val vierTokenStore = mockk<VIERTokenStore>()
        val vierErrorMessageConverter = mockk<ErrorMessageConverter<ApiResponse.Failure>>()
        val tokenRefreshWorkScheduler = mockk<TokenRefreshWorkScheduler>()

        val sut = VIERTokenUseCase(
            profileRepo,
            vierTokenStore,
            vierErrorMessageConverter,
            tokenRefreshWorkScheduler
        )

        val stringGen = Arb.string()
        val username = stringGen.gen()
        val password = stringGen.gen()

        `when`("logging in") {
            and("you provide empty credentials") {
                val result = sut.performLogin("", "")

                then("it should return with an error message") {
                    result shouldBe ErrorMessage(R.string.failure_generic_no_email).left()
                }

                then("it should not make a call") {
                    coVerify(exactly = 0) { profileRepo.fetchTokens("", "") }
                }
            }

            and("it fails") {
                coEvery {
                    profileRepo.fetchTokens(
                        username,
                        password
                    )
                } returns ApiResponse.Failure.Authentication.Login.left()

                val errorMessage = errorMessageArb.gen()
                coEvery {
                    vierErrorMessageConverter.mapToHumanReadableError(ApiResponse.Failure.Authentication.Login)
                } returns errorMessage

                val result = sut.performLogin(username, password)

                then("it should return the error") {
                    result shouldBe errorMessage.left()
                }
            }

            and("it is successful") {
                val token = vierTokenArb.gen()
                coEvery {
                    profileRepo.fetchTokens(
                        username,
                        password
                    )
                } returns ApiResponse.Success.Authentication.Token(token).right()

                sut.performLogin(username, password)

                then("it should have saved the vier credentials") {
                    coVerify { vierTokenStore.saveVierCredentials(username, password) }
                }

                then("it should save the token") {
                    coVerify { vierTokenStore.saveToken(token) }
                }

                then("it should schedule the background token refresh job") {
                    verify { tokenRefreshWorkScheduler.scheduleTokenRefreshVIER() }
                }
            }
        }

        `when`("refreshing") {
            and("we did not have a refresh token") {
                coEvery { vierTokenStore.token() } returns null

                val result = sut.refresh()

                then("it should return false") {
                    result shouldBe false.right()
                }
            }

            and("we have a refresh token") {
                val oldToken = vierTokenArb.gen()
                val newToken = vierTokenArb.gen()
                coEvery { vierTokenStore.token() } returns oldToken
                coEvery { profileRepo.refreshTokens(oldToken.refreshToken) } returns
                        ApiResponse.Success.Authentication.Token(newToken).right()

                val result = sut.refresh()

                then("we should have saved the tokens") {
                    coVerify { vierTokenStore.saveToken(newToken) }
                }

                then("it should return true") {
                    result shouldBe true.right()
                }
            }
        }
    }
})
