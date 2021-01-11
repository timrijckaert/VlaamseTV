package be.tapped.vlaamsetv

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import be.tapped.vlaamsetv.auth.AuthenticationNavigationConfiguration

class MainFragment : Fragment(R.layout.fragment_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO check initial navigation
        if (savedInstanceState == null) {
            val navigator = Navigator(findNavController())
            navigator.navigateToAuthenticationFlow(
                arrayOf(
                    AuthenticationNavigationConfiguration.VRT,
                    AuthenticationNavigationConfiguration.VTM
                )
            )
        }
    }
}
