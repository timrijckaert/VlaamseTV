package be.tapped.vlaamsetv

import androidx.navigation.NavController
import be.tapped.vlaamsetv.auth.AuthenticationNavigation
import be.tapped.vlaamsetv.auth.AuthenticationNavigationConfiguration
import be.tapped.vlaamsetv.auth.prefs.TokenStorage
import be.tapped.vlaamsetv.playback.PlaybackNavigation

interface RootNavigator {

    suspend fun moveToStartDestination()

    companion object {

        internal fun create(navigator: Navigator, tokenStorage: TokenStorage): RootNavigator = object : RootNavigator {
            override suspend fun moveToStartDestination() {
                val hasCredentialsForAtLeastOneBrand = tokenStorage.hasCredentialsForAtLeastOneBrand()
                if (hasCredentialsForAtLeastOneBrand) {
                    navigator.navigateToPlayback()
                } else {
                    navigator.navigateToAuthenticationFlow(
                        arrayOf(
                            AuthenticationNavigationConfiguration.VRT,
                            AuthenticationNavigationConfiguration.VTM,
                            AuthenticationNavigationConfiguration.VIER,
                        )
                    )
                }
            }
        }
    }
}

class Navigator(private val navController: NavController) : AuthenticationNavigation,
                                                            PlaybackNavigation {

    override fun navigateToAuthenticationFlow(config: Array<AuthenticationNavigationConfiguration>) {
        navController.navigate(MainFragmentDirections.actionMainFragmentToAuthenticationFlowTv(config))
    }

    override fun navigateToPlayback() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToPlaybackActivity())
    }
}
