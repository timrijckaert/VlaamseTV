package be.tapped.vlaamsetv.auth

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.Navigator
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.prefs.AesCipherProvider
import be.tapped.vlaamsetv.prefs.CryptoImpl
import be.tapped.vlaamsetv.prefs.EncryptedDataStore
import be.tapped.vrtnu.profile.ProfileRepo
import be.tapped.vrtnu.profile.TokenRepo
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class AuthenticationActivity : FragmentActivity(R.layout.activity_authentication) {

    private val app get() = application as App

    private val navHostFragment
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    private val navigator by lazy { Navigator(navHostFragment.navController) }

    private val navArgs by navArgs<AuthenticationActivityArgs>()
    private val authenticationNavigator by lazy { AuthenticationNavigator.create(navArgs.config) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            private val profileRepo: TokenRepo get() = ProfileRepo()
            private val dataStore: EncryptedDataStore = EncryptedDataStore(
                this@AuthenticationActivity,
                CryptoImpl(AesCipherProvider("VlaamseTvKey", app.keyStore, App.KEYSTORE_NAME))
            )

            override fun instantiate(cls: ClassLoader, className: String): Fragment =
                when (className) {
                    VRTAuthenticationFragment::class.java.name ->
                        VRTAuthenticationFragment(
                            VRTAuthenticationUseCase(
                                profileRepo,
                                dataStore,
                                authenticationNavigator
                            )
                        )
                    else -> super.instantiate(cls, className)
                }
        }

        lifecycleScope.launch {
            authenticationNavigator.state.collect { screen ->
                when (screen) {
                    is AuthenticationNavigator.Screen.VRT -> {
                        navigator.navigateToVRTAuthenticationFlow(
                            VRTAuthenticationFragment.Configuration(
                                R.string.auth_flow_vrtnu_title,
                                R.string.auth_flow_vrtnu_description,
                                R.string.auth_flow_vrtnu_step_breadcrumb,
                                R.drawable.vrt_nu_logo,
                                screen.secondaryButtonText
                            )
                        )
                    }
                    is AuthenticationNavigator.Screen.VTM -> {
                        throw IllegalStateException("Not able to navigate to VTM yet!")
                    }
                    AuthenticationNavigator.Screen.End -> finishAfterTransition()
                }
            }
        }
    }
}

sealed class AuthenticationNavigationConfiguration : Parcelable {
    @Parcelize
    object VRT : AuthenticationNavigationConfiguration()

    @Parcelize
    object VTM : AuthenticationNavigationConfiguration()
}
