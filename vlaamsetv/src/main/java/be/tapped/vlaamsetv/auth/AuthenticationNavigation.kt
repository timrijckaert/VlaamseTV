package be.tapped.vlaamsetv.auth

interface AuthenticationNavigation {
    fun navigateToAuthenticationFlow(config: Array<AuthenticationNavigationConfiguration>)
    fun navigateToVRTAuthenticationFlow(config: VRTAuthenticationFragment.Configuration)
}
