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

class VRTAuthenticationUIControllerTest : BehaviorSpec({

                                                           given("A ${VRTAuthenticationUIController::class.simpleName}") {
                                                               val vrtTokenUseCase = mockk<VRTTokenUseCase>()
                                                               val authenticationNavigator = mockk<AuthenticationNavigator>()
                                                               val authenticationState = mockk<AuthenticationState>()
                                                               val sut = VRTAuthenticationUIController(vrtTokenUseCase,
                                                                                                       authenticationNavigator,
                                                                                                       authenticationState)

                                                               val stringGen = Arb.string(1)
                                                               val username = stringGen.gen()
                                                               val password = stringGen.gen()
                                                               `when`("logging in") {
                                                                   and("it was successful") {
                                                                       coEvery {
                                                                           vrtTokenUseCase.performLogin(username, password)
                                                                       } returns Unit.right()

                                                                       sut.login(username, password)

                                                                       then("it should have navigated to the next screen") {
                                                                           coVerify { authenticationNavigator.navigateNext() }
                                                                       }

                                                                       then("it should have updated the authentication state") {
                                                                           verify {
                                                                               authenticationState.updateAuthenticationState(
                                                                                   AuthenticationState.Brand.VRT,
                                                                                   AuthenticationState.Type.LOGGED_IN)
                                                                           }
                                                                       }
                                                                   }

                                                                   and("it was not successful") {
                                                                       val errorMessage = errorMessageArb.gen()
                                                                       coEvery {
                                                                           vrtTokenUseCase.performLogin(username, password)
                                                                       } returns errorMessage.left()

                                                                       sut.login(username, password)

                                                                       then("it should have updated the state") {
                                                                           verify {
                                                                               authenticationNavigator.navigateToErrorScreen(
                                                                                   errorMessage)
                                                                           }
                                                                       }
                                                                   }
                                                               }

                                                               `when`("skipping") {
                                                                   sut.next()

                                                                   then("it should navigate to the next screen") {
                                                                       coVerify { authenticationNavigator.navigateNext() }
                                                                   }

                                                                   then("it should have set the authentication state") {
                                                                       verify {
                                                                           authenticationState.updateAuthenticationState(
                                                                               AuthenticationState.Brand.VRT,
                                                                               AuthenticationState.Type.SKIPPED)
                                                                       }
                                                                   }
                                                               }
                                                           }
                                                       })

