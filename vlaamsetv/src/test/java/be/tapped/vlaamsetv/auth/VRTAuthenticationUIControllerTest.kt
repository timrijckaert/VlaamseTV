package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.*
import be.tapped.vrtnu.ApiResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.*

class VRTAuthenticationUIControllerTest : BehaviorSpec({

    given("A ${VRTAuthenticationUIController::class.simpleName}") {
        val vrtTokenUseCase = mockk<VRTTokenUseCase>()
        val authenticationNavigator = mockk<AuthenticationNavigator>()
        val errorMessageConverter = mockk<VRTErrorMessageConverter>()
        val sut = VRTAuthenticationUIController(
            vrtTokenUseCase,
            authenticationNavigator
        )

        val username = Arb.string().gen()
        val password = Arb.string().gen()
        `when`("logging in") {
            and("a blank username and password was provided") {

                sut.login("", "")

                then("it should not make a call") {
                    coVerify(exactly = 0) { vrtTokenUseCase.performLogin("", "") }
                }

                then("it should navigate to the error screen") {
                    verify { authenticationNavigator.navigateToErrorScreen(ErrorMessage(R.string.failure_generic_no_email)) }
                }
            }

            coEvery {
                vrtTokenUseCase.performLogin(username, password)
            } returns Unit.right()

            sut.login(username, password)

            and("it was successful") {
                then("it should have navigated to the next screen") {
                    coVerify { authenticationNavigator.navigateNext() }
                }
            }

            and("it was not successful") {
                val errorMessage = errorMessageArb.gen()
                every { errorMessageConverter.mapToHumanReadableError(ApiResponse.Failure.EmptyJson) } returns errorMessage
                coEvery {
                    vrtTokenUseCase.performLogin(username, password)
                } returns errorMessageArb.gen().left()

                sut.login(username, password)

                then("it should have updated the state") {
                    verify {
                        authenticationNavigator.navigateToErrorScreen(errorMessage)
                    }
                }
            }
        }

        `when`("skipping") {
            sut.next()

            then("it should navigate to the next screen") {
                coVerify { authenticationNavigator.navigateNext() }
            }
        }
    }
})

