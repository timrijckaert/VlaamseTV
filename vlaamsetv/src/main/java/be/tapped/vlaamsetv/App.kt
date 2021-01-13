package be.tapped.vlaamsetv

import android.app.Application
import be.tapped.vlaamsetv.prefs.AesCipherProvider
import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.CryptoImpl
import java.security.KeyStore

class App : Application() {

    companion object {
        private const val KEYSTORE_NAME: String = "AndroidKeyStore"
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(KEYSTORE_NAME).apply { load(null) }
    }

    val crypto: Crypto by lazy {
        CryptoImpl(
            AesCipherProvider(
                "VlaamseTvKey",
                keyStore,
                KEYSTORE_NAME
            )
        )
    }
}
