package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.errorMessageArb
import be.tapped.vlaamsetv.gen
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify

class VIERAuthenticationUIControllerTest : BehaviorSpec() {
    init {
        given("A ${VIERAuthenticationUIController::class.java.simpleName}") {
            val vierTokenUseCase = mockk<VIERTokenUseCase>()
            val authenticationNavigator = mockk<AuthenticationNavigator>()
            val sut = VIERAuthenticationUIController(
                vierTokenUseCase,
                authenticationNavigator,
            )

            val username = Arb.string().gen()
            val password = Arb.string().gen()
            `when`("logging in") {
                coEvery {
                    vierTokenUseCase.performLogin(
                        username,
                        password
                    )
                } returns Unit.right()

                sut.login(username, password)

                and("it was successful") {
                    then("it should have navigated to the next screen") {
                        coVerify { authenticationNavigator.navigateNext() }
                    }
                }

                and("it was not successful") {
                    val errorMessage = errorMessageArb.gen()
                    coEvery {
                        vierTokenUseCase.performLogin(username, password)
                    } returns errorMessage.left()

                    sut.login(username, password)

                    then("it should navigate to the error screen") {
                        verify { authenticationNavigator.navigateToErrorScreen(errorMessage) }
                    }
                }
            }

            `when`("skipping") {
                sut.next()

                then("it should navigate to the next screen") {
                    coVerify { authenticationNavigator.navigateNext() }
                }
            }

            `when`("the UI was shown to the user") {
                and("we already have credentials") {
                    coEvery { vierTokenUseCase.hasCredentials() } returns true
                    sut.onUIShown()
                    then("it should navigate to the next screen") {
                        verify { authenticationNavigator.navigateNext() }
                    }
                }

                and("we don't have credentials") {
                    coEvery { vierTokenUseCase.hasCredentials() } returns false
                    sut.onUIShown()
                    then("it should not navigate to the next screen") {
                        verify(exactly = 0) { authenticationNavigator.navigateNext() }
                    }
                }
            }
        }
    }
}
