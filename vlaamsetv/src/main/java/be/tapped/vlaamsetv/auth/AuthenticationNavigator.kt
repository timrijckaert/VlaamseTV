package be.tapped.vlaamsetv.auth

import androidx.navigation.NavController
import be.tapped.vlaamsetv.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

interface AuthenticationNavigator {
    sealed class Screen {
        data class VRT(val secondaryButtonText: Int) : Screen()
        data class VTM(val secondaryButtonText: Int) : Screen()
        object End : Screen()
    }

    val state: Flow<Screen>

    suspend fun navigateNext()

    suspend fun navigateBack()

    fun navigateToVRTLoginFlow(config: DefaultLoginConfiguration)

    fun navigateToVTMLoginFlow(config: DefaultLoginConfiguration)

    companion object {
        internal fun create(
            navController: NavController,
            authenticationScreenConfig: Array<AuthenticationNavigationConfiguration>
        ): AuthenticationNavigator {
            return object : AuthenticationNavigator {
                private val _state: MutableSharedFlow<IndexedScreen> = MutableSharedFlow(replay = 1)
                override val state: Flow<Screen> get() = _state.map { it.second }

                private val current get() = _state.replayCache.first()

                private val firstPage
                    get() = 0 to authenticationScreenConfig.first().calculateNextScreen(1)

                init {
                    check(authenticationScreenConfig.isNotEmpty()) { "An empty authentication screen configuration was provided!" }
                    _state.tryEmit(firstPage)
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

                    val newAuthenticationPage =
                        if (newIndex < 0 || newIndex >= authenticationScreenConfig.size) {
                            Screen.End
                        } else {
                            authenticationScreenConfig[newIndex].calculateNextScreen(newIndex)
                        }
                    _state.emit(newIndex to newAuthenticationPage)
                }

                private fun AuthenticationNavigationConfiguration.calculateNextScreen(nextIndex: Int): Screen {
                    val isLastItem = nextIndex >= authenticationScreenConfig.size
                    val secondaryButtonText =
                        if (isLastItem) R.string.auth_flow_finish else R.string.auth_flow_skip
                    return when (this) {
                        AuthenticationNavigationConfiguration.VRT ->
                            Screen.VRT(secondaryButtonText)
                        AuthenticationNavigationConfiguration.VTM ->
                            Screen.VTM(secondaryButtonText)
                    }
                }
            }
        }
    }
}

typealias IndexedScreen = Pair<Int, AuthenticationNavigator.Screen>
