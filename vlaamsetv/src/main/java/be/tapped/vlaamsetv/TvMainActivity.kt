package be.tapped.vlaamsetv

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import be.tapped.vlaamsetv.App.Companion.KEYSTORE_NAME
import be.tapped.vlaamsetv.auth.AuthenticationCoordinatorFragment
import be.tapped.vlaamsetv.auth.AuthenticationFragment
import be.tapped.vlaamsetv.auth.VRTAuthenticationUseCase
import be.tapped.vlaamsetv.prefs.AesCipherProvider
import be.tapped.vlaamsetv.prefs.CryptoImpl
import be.tapped.vlaamsetv.prefs.EncryptedDataStore
import be.tapped.vrtnu.profile.ProfileRepo
import be.tapped.vrtnu.profile.TokenRepo

class TvMainActivity : FragmentActivity(R.layout.activity_tv_main) {

    private val app get() = application as App

    override fun onCreate(savedInstanceState: Bundle?) {
        val context = this
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            private val profileRepo: TokenRepo get() = ProfileRepo()
            private val dataStore: EncryptedDataStore = EncryptedDataStore(
                context,
                CryptoImpl(AesCipherProvider("VlaamseTvKey", app.keyStore, KEYSTORE_NAME))
            )

            override fun instantiate(cls: ClassLoader, className: String): Fragment {
                return when (className) {
                    AuthenticationCoordinatorFragment::class.java.name ->
                        AuthenticationCoordinatorFragment()
                    AuthenticationFragment::class.java.name ->
                        AuthenticationFragment(VRTAuthenticationUseCase(profileRepo, dataStore))
                    else -> super.instantiate(cls, className)
                }
            }
        }
        super.onCreate(savedInstanceState)
    }
}
