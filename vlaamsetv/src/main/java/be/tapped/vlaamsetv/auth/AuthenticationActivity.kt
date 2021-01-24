package be.tapped.vlaamsetv.auth

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.navigation.navArgs
import androidx.work.WorkManager
import be.tapped.vlaamsetv.*
import be.tapped.vrtnu.profile.ProfileRepo
import be.tapped.vtmgo.profile.HttpAuthenticationRepo
import kotlinx.parcelize.Parcelize
import be.tapped.vier.profile.HttpProfileRepo as VierHttpProfileRepo

class AuthenticationActivity : FragmentActivity(R.layout.activity_authentication) {

    private val app get() = application as App

    private val navArgs by navArgs<AuthenticationActivityArgs>()
    private val authenticationState = AuthenticationState()
    private val authenticationNavigator by lazy {
        AuthenticationNavigator.create(this, navHostFragment.navController, navArgs.config, authenticationState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                authenticationNavigator.navigateBack()
            }
        })

        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(cls: ClassLoader, className: String): Fragment {
                val tokenRefreshWorkScheduler = TokenRefreshWorkScheduler(WorkManager.getInstance(this@AuthenticationActivity))
                return when (className) {
                    AuthenticationFragment::class.java.name ->
                        AuthenticationFragment(authenticationNavigator)
                    VRTLoginFragment::class.java.name ->
                        VRTLoginFragment(
                            VRTAuthenticationUIController(
                                VRTTokenUseCase(
                                    ProfileRepo(),
                                    app.vrtTokenStore,
                                    VRTErrorMessageConverter(),
                                    tokenRefreshWorkScheduler,
                                ),
                                authenticationNavigator,
                                authenticationState,
                            ),
                        )
                    VTMLoginFragment::class.java.name ->
                        VTMLoginFragment(
                            VTMAuthenticationUIController(
                                VTMTokenUseCase(
                                    HttpAuthenticationRepo(),
                                    app.vtmTokenStore,
                                    VTMErrorMessageConverter(),
                                    tokenRefreshWorkScheduler,
                                ),
                                authenticationNavigator,
                                authenticationState,
                            ),
                        )
                    VIERLoginFragment::class.java.name ->
                        VIERLoginFragment(
                            VIERAuthenticationUIController(
                                VIERTokenUseCase(
                                    VierHttpProfileRepo(),
                                    app.vierTokenStore,
                                    VIERErrorMessageConverter(),
                                    tokenRefreshWorkScheduler,
                                ),
                                authenticationNavigator,
                                authenticationState
                            ),
                        )
                    AuthenticationFailedDialog::class.java.name -> AuthenticationFailedDialog(authenticationNavigator)
                    else -> super.instantiate(cls, className)
                }
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
