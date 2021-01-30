package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.goplay.ApiResponse
import be.tapped.goplay.profile.ProfileRepo
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.auth.prefs.goplay.GoPlayTokenStore
import be.tapped.vlaamsetv.errorMessageArb
import be.tapped.vlaamsetv.gen
import be.tapped.vlaamsetv.goPlayTokenArb
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify

class GoPlayTokenUseCaseTest : BehaviorSpec({
    given("A ${GoPlayTokenUseCase::class.simpleName}") {
        val profileRepo = mockk<ProfileRepo>()
        val goPlayTokenStore = mockk<GoPlayTokenStore>()
        val goPlayErrorMessageConverter =
            mockk<ErrorMessageConverter<ApiResponse.Failure>>()
        val tokenRefreshWorkScheduler = mockk<TokenRefreshWorkScheduler>()

        val sut = GoPlayTokenUseCase(
            profileRepo,
            goPlayTokenStore,
            goPlayErrorMessageConverter,
            tokenRefreshWorkScheduler
        )

        val stringGen = Arb.string(minSize = 1)
        val username = stringGen.gen()
        val password = stringGen.gen()

        `when`("logging in") {
            and("it fails") {
                coEvery {
                    profileRepo.fetchTokens(username, password)
                } returns ApiResponse.Failure.Authentication.Login.left()

                val errorMessage = errorMessageArb.gen()
                coEvery {
                    goPlayErrorMessageConverter.mapToHumanReadableError(ApiResponse.Failure.Authentication.Login)
                } returns errorMessage

                val result = sut.performLogin(username, password)

                then("it should return the error") {
                    result shouldBe errorMessage.left()
                }
            }

            and("it is successful") {
                val token = goPlayTokenArb.gen()
                coEvery {
                    profileRepo.fetchTokens(username, password)
                } returns ApiResponse.Success.Authentication.Token(token).right()

                sut.performLogin(username, password)

                then("it should have saved the GoPlay credentials") {
                    coVerify { goPlayTokenStore.saveGoPlayCredentials(username, password) }
                }

                then("it should save the token") {
                    coVerify { goPlayTokenStore.saveToken(token) }
                }

                then("it should schedule the background token refresh job") {
                    verify { tokenRefreshWorkScheduler.scheduleTokenRefreshGoPlay() }
                }
            }
        }

        `when`("refreshing") {
            and("we did not have a refresh token") {
                coEvery { goPlayTokenStore.token() } returns null

                val result = sut.refresh()

                then("it should return false") {
                    result shouldBe false.right()
                }
            }

            and("we have a refresh token") {
                val oldToken = goPlayTokenArb.gen()
                val newToken = goPlayTokenArb.gen()
                coEvery { goPlayTokenStore.token() } returns oldToken
                coEvery { profileRepo.refreshTokens(oldToken.refreshToken) } returns ApiResponse.Success.Authentication
                    .Token(newToken)
                    .right()

                val result = sut.refresh()

                then("we should have saved the tokens") {
                    coVerify { goPlayTokenStore.saveToken(newToken) }
                }

                then("it should return true") {
                    result shouldBe true.right()
                }
            }
        }
    }
})
