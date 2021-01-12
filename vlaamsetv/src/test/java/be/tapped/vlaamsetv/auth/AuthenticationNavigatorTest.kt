package be.tapped.vlaamsetv.auth

import androidx.navigation.NavController
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.flow.first

class AuthenticationNavigatorTest : FreeSpec() {

    init {
        "An ${AuthenticationNavigatorTest::class.java.simpleName}" - {
            val navController: NavController = mockk()

            "and it has only 1 destination" - {
                val authenticationScreenConfig =
                    arrayOf<AuthenticationNavigationConfiguration>(
                        AuthenticationNavigationConfiguration.VRT
                    )

                val sut = AuthenticationNavigator.create(navController, authenticationScreenConfig)

                "when connecting to the state" - {
                    "then it should emit the first screen" - {
                        sut.state.first() shouldBe AuthenticationNavigator.Screen.VRT(true)
                    }
                }

                "when navigating forward" - {
                    sut.navigateNext()
                    "then it should close the authentication flow"  {
                        sut.state.first() shouldBe AuthenticationNavigator.Screen.End
                    }
                }

                "when navigating back" - {
                    sut.navigateBack()
                    "then it should stay on the initial screen" - {
                        sut.state.first() shouldBe AuthenticationNavigator.Screen.VRT(true)
                    }
                }
            }

            "and it has multiple destinations" - {
                val authenticationScreenConfig =
                    arrayOf(
                        AuthenticationNavigationConfiguration.VRT,
                        AuthenticationNavigationConfiguration.VTM,
                    )

                val sut = AuthenticationNavigator.create(navController, authenticationScreenConfig)

                "when connecting to the state" - {
                    "then it should emit the first screen" - {
                        sut.state.first() shouldBe AuthenticationNavigator.Screen.VRT(false)
                    }
                }

                "when navigating forward" - {
                    sut.navigateNext()
                    "then it should navigate to the next screen" - {
                        sut.state.first() shouldBe AuthenticationNavigator.Screen.VTM(true)
                    }
                }

                "when navigating back" - {
                    sut.navigateBack()
                    "then it should stay on the initial screen" - {
                        sut.state.first() shouldBe AuthenticationNavigator.Screen.VRT(false)
                    }
                }
            }
        }
    }
}
