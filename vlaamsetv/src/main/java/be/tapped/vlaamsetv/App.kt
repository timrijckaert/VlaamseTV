package be.tapped.vlaamsetv

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import be.tapped.vlaamsetv.auth.AuthenticationWorkerFactory
import be.tapped.vlaamsetv.prefs.AesCipherProvider
import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.CryptoImpl
import java.security.KeyStore

class App : Application(), Configuration.Provider {

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

    override fun onCreate() {
        super.onCreate()
        // TODO schedule the token refresh tasks
        WorkManager.getInstance(this)
    }

    override fun getWorkManagerConfiguration(): Configuration =
            Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.DEBUG)
                    .setWorkerFactory(WorkerFactory(listOf(AuthenticationWorkerFactory)))
                    .build()
}
