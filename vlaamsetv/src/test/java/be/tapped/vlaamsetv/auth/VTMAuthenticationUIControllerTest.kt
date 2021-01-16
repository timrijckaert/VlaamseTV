package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.*
import be.tapped.vtmgo.ApiResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.*

class VTMAuthenticationUIControllerTest : BehaviorSpec() {
    init {
        given("A ${VTMAuthenticationUIController::class.java.simpleName}") {
            val vtmTokenUseCase = mockk<VTMTokenUseCase>()
            val authenticationNavigator = mockk<AuthenticationNavigator>()
            val errorMessageConverter = mockk<ErrorMessageConverter<ApiResponse.Failure>>()
            val sut = VTMAuthenticationUIController(
                vtmTokenUseCase,
                authenticationNavigator,
            )

            val username = Arb.string().gen()
            val password = Arb.string().gen()
            `when`("logging in") {
                and("a blank username and password was provided") {

                    sut.login("", "")

                    then("it should not make a call") {
                        coVerify(exactly = 0) { vtmTokenUseCase.performLogin("", "") }
                    }

                    then("it should navigate to the error screen") {
                        verify { authenticationNavigator.navigateToErrorScreen(ErrorMessage(R.string.failure_generic_no_email)) }
                    }
                }

                coEvery {
                    vtmTokenUseCase.performLogin(
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
                    every { errorMessageConverter.mapToHumanReadableError(ApiResponse.Failure.EmptyJson) } returns errorMessage
                    coEvery {
                        vtmTokenUseCase.performLogin(username, password)
                    } returns errorMessageArb.gen().left()

                    sut.login(username, password)

                    then("it should navigate to the error screen") {
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
    }
}
