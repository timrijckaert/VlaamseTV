package be.tapped.vlaamsetv

import androidx.navigation.NavController
import be.tapped.vlaamsetv.auth.AuthenticationNavigation
import be.tapped.vlaamsetv.auth.AuthenticationNavigationConfiguration
import be.tapped.vlaamsetv.auth.VRTLoginFragment
import be.tapped.vlaamsetv.auth.VRTLoginFragmentArgs

class Navigator(private val navController: NavController) : AuthenticationNavigation {

    override fun navigateToAuthenticationFlow(config: Array<AuthenticationNavigationConfiguration>) {
        navController.navigate(
            MainFragmentDirections.actionMainFragmentToAuthenticationFlowTv(config)
        )
    }
}
