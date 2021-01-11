package be.tapped.vlaamsetv

import androidx.navigation.NavController
import be.tapped.vlaamsetv.auth.AuthenticationNavigation
import be.tapped.vlaamsetv.auth.AuthenticationNavigationConfiguration
import be.tapped.vlaamsetv.auth.VRTAuthenticationFragment
import be.tapped.vlaamsetv.auth.VRTAuthenticationFragmentArgs

class Navigator(private val navController: NavController) : AuthenticationNavigation {

    val currentId: Int? get() = navController.currentDestination?.id

    override fun navigateToAuthenticationFlow(config: Array<AuthenticationNavigationConfiguration>) {
        navController.navigate(
            MainFragmentDirections.actionMainFragmentToAuthenticationFlowTv(config)
        )
    }

    override fun navigateToVRTAuthenticationFlow(config: VRTAuthenticationFragment.Configuration) {
        navController.navigate(
            R.id.action_to_vrt_authentication_fragment,
            VRTAuthenticationFragmentArgs(config).toBundle()
        )
    }
}
