package be.tapped.vlaamsetv.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthenticationFragment(private val authenticationNavigator: AuthenticationNavigator) :
    Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            authenticationNavigator.state.collect { screen ->
                when (screen) {
                    is AuthenticationNavigator.Screen.VRT -> {
                        authenticationNavigator.navigateToVRTAuthenticationFlow(
                            VRTLoginFragment.VRTLoginConfiguration(screen.secondaryButtonText)
                        )
                    }
                    is AuthenticationNavigator.Screen.VTM -> {
                        throw IllegalStateException("Not able to navigate to VTM yet!")
                    }
                    AuthenticationNavigator.Screen.End -> requireActivity().finishAfterTransition()
                }
            }
        }
    }
}
