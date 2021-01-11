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

    fun navigateToVRTAuthenticationFlow(config: VRTLoginFragment.VRTLoginConfiguration)

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

                override fun navigateToVRTAuthenticationFlow(config: VRTLoginFragment.VRTLoginConfiguration) {
                    navController.navigate(
                        R.id.action_to_vrt_authentication_fragment,
                        VRTLoginFragmentArgs(config).toBundle()
                    )
                }

                override suspend fun navigateNext() {
                    val (index, page) = current

                    if (page == Screen.End) {
                        _state.emit(current)
                        return
                    }

                    val newIndex = index + 1

                    val newAuthenticationPage = if (newIndex >= authenticationScreenConfig.size) {
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
