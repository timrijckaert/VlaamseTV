package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.prefs.VRTTokenStore
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.profile.TokenRepo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

internal class VRTAuthenticationUseCaseTest : BehaviorSpec({

    given("A ${VRTAuthenticationUseCase::class.simpleName}") {
        val tokenRepo = mockk<TokenRepo>()
        val vrtTokenStore = mockk<VRTTokenStore>()
        val sut = VRTAuthenticationUseCase(tokenRepo, vrtTokenStore)

        val username = Arb.string().gen()
        val password = Arb.string().gen()
        `when`("logging in") {
            and("a blank username and password was provided") {

                sut.login("", "")

                then("it should not make a call") {
                    coVerify(exactly = 0) { tokenRepo.fetchTokenWrapper("", "") }
                }

                then("it should update the state") {
                    sut.state.first() shouldBe AuthenticationUseCase.State.Fail("Je hebt geen email adres ingevoerd")
                }
            }

            val tokenWrapper = tokenWrapperArb.gen()
            coEvery {
                tokenRepo.fetchTokenWrapper(username, password)
            } returns ApiResponse.Success.Authentication.Token(tokenWrapper).right()

            sut.login(username, password)

            and("it was successful") {
                then("it should save the retrieved token wrapper") {
                    coVerify { vrtTokenStore.saveTokenWrapper(tokenWrapper) }
                }

                then("it should have updated the state") {
                    sut.state.first() shouldBe AuthenticationUseCase.State.Successful
                }
            }

            and("it was not successful") {
                coEvery {
                    tokenRepo.fetchTokenWrapper(username, password)
                } returns ApiResponse.Failure.EmptyJson.left()

                sut.login(username, password)

                then("it should have updated the state") {
                    sut.state.first() shouldBe AuthenticationUseCase.State.Fail("No JSON response")
                }
            }
        }
    }
})

