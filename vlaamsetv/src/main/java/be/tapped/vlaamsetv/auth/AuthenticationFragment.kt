package be.tapped.vlaamsetv.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.exhaustive
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
                        authenticationNavigator.navigateToVRTLoginFlow(
                            DefaultLoginConfiguration(screen.secondaryButtonText)
                        )
                    }
                    is AuthenticationNavigator.Screen.VTM -> {
                        authenticationNavigator.navigateToVTMLoginFlow(
                            DefaultLoginConfiguration(screen.secondaryButtonText)
                        )
                    }
                    is AuthenticationNavigator.Screen.VIER -> {
                        authenticationNavigator.navigateToVIERLoginFlow(
                            DefaultLoginConfiguration(screen.secondaryButtonText)
                        )
                    }
                    AuthenticationNavigator.Screen.End -> requireActivity().finishAfterTransition()
                }.exhaustive
            }
        }
    }
}
