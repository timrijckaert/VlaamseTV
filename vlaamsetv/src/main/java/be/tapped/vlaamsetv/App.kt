package be.tapped.vlaamsetv

import android.app.Application
import androidx.work.Configuration
import be.tapped.vlaamsetv.auth.AuthenticationWorkerFactory
import be.tapped.vlaamsetv.auth.prefs.vier.VIERTokenStore
import be.tapped.vlaamsetv.auth.prefs.vier.VIERTokenStoreImpl
import be.tapped.vlaamsetv.auth.prefs.vrt.VRTTokenStore
import be.tapped.vlaamsetv.auth.prefs.vrt.VRTTokenStoreImpl
import be.tapped.vlaamsetv.auth.prefs.vtm.VTMTokenStore
import be.tapped.vlaamsetv.auth.prefs.vtm.VTMTokenStoreImpl
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

    private val crypto: Crypto by lazy {
        CryptoImpl(
            AesCipherProvider(
                "VlaamseTvKey",
                keyStore,
                KEYSTORE_NAME
            )
        )
    }

    val vrtTokenStore: VRTTokenStore by lazy {
        VRTTokenStoreImpl(
            this@App,
            crypto
        )
    }

    val vtmTokenStore: VTMTokenStore by lazy {
        VTMTokenStoreImpl(
            this@App,
            crypto
        )
    }

    val vierTokenStore: VIERTokenStore by lazy {
        VIERTokenStoreImpl(
            this@App,
            crypto
        )
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(WorkerFactory(listOf(AuthenticationWorkerFactory)))
            .build()
}
