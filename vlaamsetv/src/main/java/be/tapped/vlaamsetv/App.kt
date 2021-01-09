package be.tapped.vlaamsetv

import android.app.Application
import java.security.KeyStore

public class App : Application() {

    public companion object {
        public const val KEYSTORE_NAME: String = "AndroidKeyStore"
    }

    public val keyStore: KeyStore by lazy {
        KeyStore.getInstance(KEYSTORE_NAME).apply { load(null) }
    }

    override fun onCreate() {
        super.onCreate()
    }
}
