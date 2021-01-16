package be.tapped.vlaamsetv.auth

import android.os.Bundle
import androidx.fragment.app.Fragment

class AuthenticationFragment(private val authenticationNavigator: AuthenticationNavigator) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticationNavigator.moveToStartDestination()
    }
}
