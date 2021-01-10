package be.tapped.vlaamsetv.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import be.tapped.vlaamsetv.R

class AuthenticationCoordinatorFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        findNavController().navigate(
            AuthenticationCoordinatorFragmentDirections.actionAuthenticationCoordinatorFragmentToAuthenticationFragment(
                AuthenticationFragment.Configuration(
                    R.string.auth_flow_vrtnu_title,
                    R.string.auth_flow_vrtnu_description,
                    R.string.auth_flow_vrtnu_step_breadcrumb,
                    R.drawable.vrt_nu_logo
                )
            )
        )
        super.onCreate(savedInstanceState)
    }
}
