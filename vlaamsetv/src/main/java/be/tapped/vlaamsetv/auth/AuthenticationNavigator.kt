package be.tapped.vlaamsetv.auth

import androidx.navigation.NavController
import be.tapped.vlaamsetv.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

interface AuthenticationNavigator {
    sealed class Screen {
        data class VRT(val isLastScreen: Boolean) : Screen()
        data class VTM(val isLastScreen: Boolean) : Screen()
        data class VIER(val isLastScreen: Boolean) : Screen()
        object End : Screen()
    }

    val state: Flow<Screen>

    suspend fun navigateNext()

    suspend fun navigateBack()

    fun navigateToVRTLoginFlow(config: DefaultLoginConfiguration)

    fun navigateToVTMLoginFlow(config: DefaultLoginConfiguration)

    fun navigateToVIERLoginFlow(config: DefaultLoginConfiguration)

    companion object {
        internal fun create(
            navController: NavController,
            authenticationScreenConfig: Array<AuthenticationNavigationConfiguration>
        ): AuthenticationNavigator {
            return object : AuthenticationNavigator {
                private val _state: MutableSharedFlow<IndexedScreen> = MutableSharedFlow(replay = 1)
                override val state: Flow<Screen> get() = _state.map { it.second }

                private val current get() = _state.replayCache.first()

                init {
                    check(authenticationScreenConfig.isNotEmpty()) { "An empty authentication screen configuration was provided!" }
                    _state.tryEmit(0 to authenticationScreenConfig.first().calculateNextScreen(1))
                }

                override fun navigateToVRTLoginFlow(config: DefaultLoginConfiguration) {
                    navController.navigate(
                        R.id.action_to_vrt_login_fragment,
                        VRTLoginFragmentArgs(config).toBundle()
                    )
                }

                override fun navigateToVTMLoginFlow(config: DefaultLoginConfiguration) {
                    navController.navigate(
                        R.id.action_to_vtm_login_fragment,
                        VTMLoginFragmentArgs(config).toBundle()
                    )
                }

                override fun navigateToVIERLoginFlow(config: DefaultLoginConfiguration) {
                    navController.navigate(
                        R.id.action_to_vier_login_fragment,
                        VIERLoginFragmentArgs(config).toBundle()
                    )
                }

                override suspend fun navigateNext() {
                    navigate { it + 1 }
                }

                override suspend fun navigateBack() {
                    navigate { it - 1 }
                }

                private suspend fun navigate(indexFunc: (Int) -> Int) {
                    val (index, page) = current

                    if (page == Screen.End) {
                        _state.emit(current)
                        return
                    }

                    val newIndex = indexFunc(index)

                    if (newIndex < 0) {
                        return
                    }

                    val newAuthenticationPage =
                        if (newIndex >= authenticationScreenConfig.size) {
                            Screen.End
                        } else {
                            authenticationScreenConfig[newIndex].calculateNextScreen(newIndex + 1)
                        }
                    _state.emit(newIndex to newAuthenticationPage)
                }

                private fun AuthenticationNavigationConfiguration.calculateNextScreen(nextIndex: Int): Screen {
                    val isLastItem = nextIndex >= authenticationScreenConfig.size
                    return when (this) {
                        AuthenticationNavigationConfiguration.VRT ->
                            Screen.VRT(isLastItem)
                        AuthenticationNavigationConfiguration.VTM ->
                            Screen.VTM(isLastItem)
                        AuthenticationNavigationConfiguration.VIER ->
                            Screen.VIER(isLastItem)
                    }
                }
            }
        }
    }
}

typealias IndexedScreen = Pair<Int, AuthenticationNavigator.Screen>
