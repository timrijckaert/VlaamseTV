package be.tapped.vlaamsetv.auth

import androidx.core.app.ComponentActivity
import androidx.navigation.NavController
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.exhaustive
import kotlin.properties.Delegates

interface AuthenticationNavigator { sealed class Screen { data class VRT(val isLastScreen: Boolean) : Screen()
    data class VTM(val isLastScreen: Boolean) : Screen()
    data class VIER(val isLastScreen: Boolean) : Screen()
    data class ErrorDialog(val errorMessage: String) : Screen()
    object End : Screen()
}

    val currentScreen: Screen

    fun navigateNext()

    fun navigateBack()

    fun navigateToErrorScreen(errorMessage: ErrorMessage)

    fun moveToStartDestination()

    companion object {

        internal fun create(
            activity: ComponentActivity,
            navController: NavController,
            authenticationScreenConfig: Array<AuthenticationNavigationConfiguration>,
            authenticationState: AuthenticationState,
        ): AuthenticationNavigator = object : AuthenticationNavigator {
            override val currentScreen: Screen get() = _currentScreen.second
            private var _currentScreen: IndexedScreen by Delegates.observable(0 to authenticationScreenConfig
                .first()
                .calculateNextScreen(1)) { _, _, newValue -> navigateToScreen(newValue) }

            init {
                check(authenticationScreenConfig.isNotEmpty()) {
                    "An empty authentication screen configuration was provided!"
                }
            }

            override fun navigateToErrorScreen(errorMessage: ErrorMessage) {
                navigate({ it + 1 }, { Screen.ErrorDialog(errorMessage.toString(activity)) })
            }

            override fun moveToStartDestination() {
                navigateToScreen(_currentScreen)
            }

            override fun navigateNext() {
                navigate({ it + 1 }, ::nextScreenFromConfiguration)
            }

            override fun navigateBack() {
                navigate({ it - 1 }) {
                    val screen = nextScreenFromConfiguration(it)
                    val isAllowedToNavigateBack = when (screen) {
                        is Screen.VRT -> authenticationState.stateForBrand(AuthenticationState.Brand.VRT) != AuthenticationState.Type.LOGGED_IN
                        is Screen.VTM -> authenticationState.stateForBrand(AuthenticationState.Brand.VTM) != AuthenticationState.Type.LOGGED_IN
                        is Screen.VIER -> authenticationState.stateForBrand(AuthenticationState.Brand.VIER) != AuthenticationState.Type.LOGGED_IN
                        is Screen.ErrorDialog -> false
                        Screen.End -> false
                    }

                    if (isAllowedToNavigateBack) screen else null
                }
            }

            private fun navigate(nextIndexFunc: (Int) -> Int, nextScreenFunc: (Int) -> Screen?) {
                val (index, page) = _currentScreen
                if (page == Screen.End) {
                    return
                }

                val newIndex = nextIndexFunc(index)
                if (newIndex < 0) {
                    return
                }

                val newAuthenticationPage = nextScreenFunc(newIndex)
                if (newAuthenticationPage != null) {
                    _currentScreen = newIndex to newAuthenticationPage
                }
            }

            private fun nextScreenFromConfiguration(newIndex: Int): Screen = if (newIndex >= authenticationScreenConfig.size) {
                Screen.End
            } else {
                authenticationScreenConfig[newIndex].calculateNextScreen(newIndex + 1)
            }

            private fun AuthenticationNavigationConfiguration.calculateNextScreen(nextIndex: Int): Screen {
                val isLastItem = nextIndex >= authenticationScreenConfig.size
                return when (this) {
                    AuthenticationNavigationConfiguration.VRT -> Screen.VRT(isLastItem)
                    AuthenticationNavigationConfiguration.VTM -> Screen.VTM(isLastItem)
                    AuthenticationNavigationConfiguration.VIER -> Screen.VIER(isLastItem)
                }
            }

            private fun navigateToScreen(newValue: IndexedScreen) = when (val screen = newValue.second) {
                is Screen.VRT ->
                    navController.navigate(
                        R.id.action_to_vrt_login_fragment,
                        VRTLoginFragmentArgs(DefaultLoginConfiguration(screen.isLastScreen)).toBundle()
                    )
                is Screen.VTM ->
                    navController.navigate(
                        R.id.action_to_vtm_login_fragment,
                        VTMLoginFragmentArgs(DefaultLoginConfiguration(screen.isLastScreen)).toBundle()
                    )
                is Screen.VIER ->
                    navController.navigate(
                        R.id.action_to_vier_login_fragment,
                        VIERLoginFragmentArgs(DefaultLoginConfiguration(screen.isLastScreen)).toBundle()
                    )
                Screen.End -> activity.finishAfterTransition()
                is Screen.ErrorDialog -> navController.navigate(R.id.action_to_authenticationFailedDialog,
                    AuthenticationFailedDialogArgs(screen.errorMessage).toBundle())
            }.exhaustive
        }
    }
}

typealias IndexedScreen = Pair<Int, AuthenticationNavigator.Screen>
