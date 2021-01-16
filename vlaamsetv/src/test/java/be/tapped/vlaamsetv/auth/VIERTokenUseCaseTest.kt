package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vier.ApiResponse
import be.tapped.vier.profile.ProfileRepo
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.auth.prefs.vier.VIERTokenStore
import be.tapped.vlaamsetv.gen
import be.tapped.vlaamsetv.vierTokenArb
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class VIERTokenUseCaseTest : BehaviorSpec({
    given("A ${VIERTokenUseCase::class.simpleName}") {
        val profileRepo = mockk<ProfileRepo>()
        val vierTokenStore = mockk<VIERTokenStore>()
        val vierErrorMessageConverter = mockk<ErrorMessageConverter<ApiResponse.Failure>>()

        val sut = VIERTokenUseCase(profileRepo, vierTokenStore, vierErrorMessageConverter)

        val stringGen = Arb.string()
        val username = stringGen.gen()
        val password = stringGen.gen()

        `when`("logging in") {

            and("it fails") {
                coEvery {
                    profileRepo.fetchTokens(
                        username,
                        password
                    )
                } returns ApiResponse.Failure.Authentication.Login.left()

                val result = sut.performLogin(username, password)

                then("it should return the error") {
                    result shouldBe ApiResponse.Failure.Authentication.Login.left()
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

                then("it should have saved the vrt credentials") {
                    coVerify { vierTokenStore.saveVierCredentials(username, password) }
                }

                then("it should save the token") {
                    coVerify { vierTokenStore.saveToken(token) }
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
