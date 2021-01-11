package be.tapped.vlaamsetv.auth

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
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
import kotlinx.parcelize.Parcelize

class AuthenticationActivity : FragmentActivity(R.layout.activity_authentication) {

    private val app get() = application as App

    private val navHostFragment
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

    private val navArgs by navArgs<AuthenticationActivityArgs>()
    private val authenticationNavigator by lazy {
        AuthenticationNavigator.create(
            navHostFragment.navController,
            navArgs.config
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            private val profileRepo: TokenRepo get() = ProfileRepo()
            private val dataStore: EncryptedDataStore = EncryptedDataStore(
                this@AuthenticationActivity,
                CryptoImpl(AesCipherProvider("VlaamseTvKey", app.keyStore, App.KEYSTORE_NAME))
            )

            override fun instantiate(cls: ClassLoader, className: String): Fragment =
                when (className) {
                    AuthenticationFragment::class.java.name ->
                        AuthenticationFragment(
                            authenticationNavigator
                        )
                    VRTLoginFragment::class.java.name ->
                        VRTLoginFragment(
                            VRTAuthenticationUseCase(
                                profileRepo,
                                dataStore,
                                authenticationNavigator
                            )
                        )
                    else -> super.instantiate(cls, className)
                }
        }
        super.onCreate(savedInstanceState)
    }
}

sealed class AuthenticationNavigationConfiguration : Parcelable {
    @Parcelize
    object VRT : AuthenticationNavigationConfiguration()

    @Parcelize
    object VTM : AuthenticationNavigationConfiguration()
}
