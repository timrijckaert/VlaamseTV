package be.tapped.vlaamsetv.auth

import androidx.core.app.ComponentActivity
import androidx.navigation.NavController
import be.tapped.vlaamsetv.ErrorMessage
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class AuthenticationNavigatorTest : FreeSpec() {

    init {
        "An ${AuthenticationNavigatorTest::class.java.simpleName}" - {
            val navController: NavController = mockk()

            val activity = mockk<ComponentActivity>()

            "and it has only 1 destination" - {
                val authenticationScreenConfig =
                        arrayOf<AuthenticationNavigationConfiguration>(
                                AuthenticationNavigationConfiguration.VRT
                        )

                val sut = AuthenticationNavigator.create(
                        activity,
                        navController,
                        authenticationScreenConfig
                )

                "when connecting to the state" - {
                    "then it should emit the first screen" - {
                        sut.currentScreen shouldBe AuthenticationNavigator.Screen.VRT(true)
                    }
                }

                "when navigating forward" - {
                    sut.navigateNext()
                    "then it should close the authentication flow"  {
                        sut.currentScreen shouldBe AuthenticationNavigator.Screen.End
                    }
                }

                "when navigating back" - {
                    sut.navigateBack()
                    "then it should stay on the initial screen" - {
                        sut.currentScreen shouldBe AuthenticationNavigator.Screen.VRT(true)
                    }
                }

                "when navigating to the error dialog" - {
                    val errorMessage = mockk<ErrorMessage> {
                        every { toString(activity) } returns "Some Error Message"
                    }
                    sut.navigateToErrorScreen(errorMessage)
                    "then it should navigate to the error dialog" - {
                        sut.currentScreen shouldBe AuthenticationNavigator.Screen.ErrorDialog("Some Error Message")

                        "when returning to the previous screen" - {
                            sut.navigateBack()

                            "we should be back on the previous screen" - {
                                sut.currentScreen shouldBe AuthenticationNavigator.Screen.VRT(true)
                            }
                        }
                    }
                }
            }

            "and it has multiple destinations" - {
                val authenticationScreenConfig =
                        arrayOf(
                                AuthenticationNavigationConfiguration.VRT,
                                AuthenticationNavigationConfiguration.VTM,
                        )

                val sut = AuthenticationNavigator.create(
                        activity,
                        navController,
                        authenticationScreenConfig
                )

                "when connecting to the state" - {
                    "then it should emit the first screen" - {
                        sut.currentScreen shouldBe AuthenticationNavigator.Screen.VRT(false)
                    }
                }

                "when navigating forward" - {
                    sut.navigateNext()
                    "then it should navigate to the next screen" - {
                        sut.currentScreen shouldBe AuthenticationNavigator.Screen.VTM(true)
                    }
                }

                "when navigating back" - {
                    sut.navigateBack()
                    "then it should stay on the initial screen" - {
                        sut.currentScreen shouldBe AuthenticationNavigator.Screen.VRT(false)
                    }
                }
            }
        }
    }
}
