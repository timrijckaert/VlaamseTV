package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.*
import be.tapped.vlaamsetv.auth.prefs.vrt.VRTTokenStore
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.profile.TokenRepo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class VRTTokenUseCaseTest : BehaviorSpec({
    given("A ${VRTTokenUseCase::class.java.simpleName}") {
        val tokenRepo = mockk<TokenRepo>()
        val dataStore = mockk<VRTTokenStore>()
        val vrtErrorMessageConverter = mockk<ErrorMessageConverter<ApiResponse.Failure>>()
        val sut = VRTTokenUseCase(tokenRepo, dataStore, vrtErrorMessageConverter)

        val stringArb = Arb.string()
        val username = stringArb.gen()
        val password = stringArb.gen()

        and("both login service calls succeed") {
            val tokenWrapper = vrtTokenWrapperArb.gen()
            coEvery {
                tokenRepo.fetchTokenWrapper(
                    username,
                    password
                )
            } returns ApiResponse.Success.Authentication.Token(tokenWrapper).right()

            val xVRTToken = xVRTTokenArb.gen()
            coEvery {
                tokenRepo.fetchXVRTToken(
                    username,
                    password
                )
            } returns ApiResponse.Success.Authentication.VRTToken(xVRTToken).right()

            `when`("performing a login") {
                sut.performLogin(username, password)

                then("it should save the credentials") {
                    coVerify { dataStore.saveVRTCredentials(username, password) }
                }

                then("it should save the token wrapper") {
                    coVerify { dataStore.saveTokenWrapper(tokenWrapper) }
                }

                then("it should save X-VRT-Token") {
                    coVerify { dataStore.saveXVRTToken(xVRTToken) }
                }
            }
        }

        and("one service fails") {
            coEvery {
                tokenRepo.fetchTokenWrapper(
                    username,
                    password
                )
            } returns ApiResponse.Failure.EmptyJson.left()

            val xVRTToken = xVRTTokenArb.gen()
            coEvery {
                tokenRepo.fetchXVRTToken(
                    username,
                    password
                )
            } returns ApiResponse.Success.Authentication.VRTToken(xVRTToken).right()

            `when`("performing a login") {
                val result = sut.performLogin(username, password)

                then("it should return the first error") {
                    result shouldBe ApiResponse.Failure.EmptyJson.left()
                }
            }
        }

        and("refresh call fails") {
            val refreshToken = vrtRefreshTokenArb.gen()
            coEvery { dataStore.token() } returns vrtTokenWrapperArb.gen()
                .copy(refreshToken = refreshToken)
            coEvery { tokenRepo.refreshTokenWrapper(refreshToken) } returns ApiResponse.Failure.EmptyJson.left()

            `when`("refreshing the token") {

                then("it should return the failure") {
                    sut.refresh() shouldBe ApiResponse.Failure.EmptyJson.left()
                }
            }
        }

        and("refresh call works") {
            val refreshToken = vrtRefreshTokenArb.gen()
            val oldToken = vrtTokenWrapperArb.gen()
            coEvery { dataStore.token() } returns oldToken.copy(refreshToken = refreshToken)
            val newToken = vrtTokenWrapperArb.gen()
            coEvery { tokenRepo.refreshTokenWrapper(refreshToken) } returns ApiResponse.Success.Authentication.Token(
                newToken
            ).right()

            `when`("refreshing") {
                val result = sut.refresh()

                then("it should have saved the new token") {
                    coVerify { dataStore.saveTokenWrapper(newToken) }
                }

                then("it should return true indicating success") {
                    result shouldBe true.right()
                }
            }
        }
    }
})
