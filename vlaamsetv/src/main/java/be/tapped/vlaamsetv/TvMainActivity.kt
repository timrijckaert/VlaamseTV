package be.tapped.vlaamsetv

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.App.Companion.KEYSTORE_NAME
import be.tapped.vlaamsetv.auth.VRTAuthenticationFragment
import be.tapped.vlaamsetv.auth.VRTAuthenticationUseCase
import be.tapped.vlaamsetv.prefs.AesCipherProvider
import be.tapped.vlaamsetv.prefs.CryptoImpl
import be.tapped.vlaamsetv.prefs.EncryptedDataStore
import be.tapped.vrtnu.profile.ProfileRepo
import be.tapped.vrtnu.profile.TokenRepo
import kotlinx.coroutines.launch

public class TvMainActivity : FragmentActivity(R.layout.activity_tv_main) {

    private val app get() = application as App

    override fun onCreate(savedInstanceState: Bundle?) {
        val context = this
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            private val profileRepo: TokenRepo = ProfileRepo()
            private val crypto =
                CryptoImpl(AesCipherProvider("VlaamseTvKey", app.keyStore, KEYSTORE_NAME))
            private val dataStore: EncryptedDataStore = EncryptedDataStore(context, crypto)

            override fun instantiate(cls: ClassLoader, className: String): Fragment {
                return when (className) {
                    VRTAuthenticationFragment::class.java.name ->
                        VRTAuthenticationFragment(VRTAuthenticationUseCase(profileRepo, dataStore))
                    else -> super.instantiate(cls, className)
                }
            }
        }
        super.onCreate(savedInstanceState)
    }
}
