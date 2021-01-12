package be.tapped.vlaamsetv.auth

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import be.tapped.vlaamsetv.*
import be.tapped.vlaamsetv.prefs.AesCipherProvider
import be.tapped.vlaamsetv.prefs.CryptoImpl
import be.tapped.vlaamsetv.prefs.EncryptedTokenDataStore
import be.tapped.vrtnu.profile.ProfileRepo
import kotlinx.parcelize.Parcelize
import be.tapped.vier.profile.HttpProfileRepo as VierHttpProfileRepo
import be.tapped.vtmgo.profile.HttpProfileRepo as VTMHttpProfileRepo

class AuthenticationActivity : FragmentActivity(R.layout.activity_authentication) {

    private val app get() = application as App

    private val navHostFragment
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

    private val navArgs by navArgs<AuthenticationActivityArgs>()
    private val authenticationNavigator by lazy {
        AuthenticationNavigator.create(
            this,
            navHostFragment.navController,
            navArgs.config
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    authenticationNavigator.navigateBack()
                }
            })

        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            private val dataStore = EncryptedTokenDataStore(
                this@AuthenticationActivity,
                CryptoImpl(
                    AesCipherProvider(
                        "VlaamseTvKey",
                        app.keyStore,
                        App.KEYSTORE_NAME
                    )
                )
            )

            override fun instantiate(cls: ClassLoader, className: String): Fragment =
                when (className) {
                    AuthenticationFragment::class.java.name ->
                        AuthenticationFragment(authenticationNavigator)
                    VRTLoginFragment::class.java.name -> {
                        VRTLoginFragment(
                            VRTAuthenticationUseCase(
                                ProfileRepo(),
                                dataStore,
                                authenticationNavigator,
                                VRTErrorMessageConverter()
                            ),
                        )
                    }
                    VTMLoginFragment::class.java.name ->
                        VTMLoginFragment(
                            VTMAuthenticationUseCase(
                                VTMHttpProfileRepo(),
                                dataStore,
                                authenticationNavigator,
                                VTMErrorMessageConverter()
                            ),
                        )
                    VIERLoginFragment::class.java.name ->
                        VIERLoginFragment(
                            VIERAuthenticationUseCase(
                                VierHttpProfileRepo(),
                                dataStore,
                                authenticationNavigator,
                                VIERErrorMessageConverter()
                            ),
                        )
                    AuthenticationFailedDialog::class.java.name ->
                        AuthenticationFailedDialog(authenticationNavigator)
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

    @Parcelize
    object VIER : AuthenticationNavigationConfiguration()
}
