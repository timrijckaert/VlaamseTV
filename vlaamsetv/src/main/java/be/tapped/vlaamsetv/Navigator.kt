package be.tapped.vlaamsetv

import androidx.navigation.NavController
import be.tapped.vlaamsetv.auth.AuthenticationNavigation
import be.tapped.vlaamsetv.auth.AuthenticationNavigationConfiguration
import be.tapped.vlaamsetv.auth.prefs.TokenStorage

interface RootNavigator {

    suspend fun moveToStartDestination()

    companion object {
        internal fun create(
            navigator: Navigator,
            tokenStorage: TokenStorage
        ): RootNavigator =
            object : RootNavigator {
                override suspend fun moveToStartDestination() {
                    val hasCredentialsForAtLeastOneBrand =
                        tokenStorage.hasCredentialsForAtLeastOneBrand()
                    if (hasCredentialsForAtLeastOneBrand) {
                        //TODO in another story.
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

class Navigator(private val navController: NavController) : AuthenticationNavigation {

    override fun navigateToAuthenticationFlow(config: Array<AuthenticationNavigationConfiguration>) {
        navController.navigate(
            MainFragmentDirections.actionMainFragmentToAuthenticationFlowTv(config)
        )
    }
}
