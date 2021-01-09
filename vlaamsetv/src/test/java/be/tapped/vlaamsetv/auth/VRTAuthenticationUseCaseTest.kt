package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.prefs.VRTTokenStore
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.profile.TokenRepo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

internal class VRTAuthenticationUseCaseTest : BehaviorSpec({

    given("A ${VRTAuthenticationUseCase::class.simpleName}") {
        val tokenRepo = mockk<TokenRepo>()
        val vrtTokenStore = mockk<VRTTokenStore>()
        val sut = VRTAuthenticationUseCase(tokenRepo, vrtTokenStore)
        `when`("logging in") {
            val tokenWrapper = tokenWrapperArb.gen()
            coEvery {
                tokenRepo.fetchTokenWrapper(
                    any(),
                    any()
                )
            } returns ApiResponse.Success.Authentication.Token(tokenWrapper).right()

            sut.login()

            and("it was successful") {
                then("it should save the retrieved token wrapper") {
                    coVerify { vrtTokenStore.saveTokenWrapper(tokenWrapper) }
                }

                then("it should have updated the state") {
                    sut.state.value shouldBe AuthenticationUseCase.State.Successful
                }
            }

            and("it was not successful") {
                coEvery {
                    tokenRepo.fetchTokenWrapper(
                        any(),
                        any()
                    )
                } returns ApiResponse.Failure.EmptyJson.left()

                sut.login()

                then("it should have updated the state") {
                    sut.state.value shouldBe AuthenticationUseCase.State.Fail("No JSON response")
                }
            }
        }
    }
})
