package be.tapped.vlaamsetv

import android.app.Application
import java.security.KeyStore

class App : Application() {

    companion object {
        const val KEYSTORE_NAME: String = "AndroidKeyStore"
    }

    val keyStore: KeyStore by lazy {
        KeyStore.getInstance(KEYSTORE_NAME).apply { load(null) }
    }
}
