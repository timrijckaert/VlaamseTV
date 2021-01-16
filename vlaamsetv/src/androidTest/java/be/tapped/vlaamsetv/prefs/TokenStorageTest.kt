package be.tapped.vlaamsetv.prefs

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.auth.prefs.CompositeTokenStorage
import be.tapped.vlaamsetv.auth.prefs.TokenStorage
import be.tapped.vlaamsetv.auth.prefs.vier.VIERTokenStoreImpl
import be.tapped.vlaamsetv.auth.prefs.vrt.VRTTokenStoreImpl
import be.tapped.vlaamsetv.auth.prefs.vtm.VTMTokenStoreImpl
import be.tapped.vlaamsetv.gen
import be.tapped.vlaamsetv.vierTokenArb
import be.tapped.vlaamsetv.vrtTokenWrapperArb
import be.tapped.vlaamsetv.vtmTokenWrapperArb
import be.tapped.vrtnu.profile.Expiry
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyStore

@RunWith(AndroidJUnit4::class)
internal class TokenStorageTest {

    companion object {

        private const val ONE_HOUR = 1 * 60 * 60 * 1000
    }

    private val crypto
        get() = CryptoImpl(AesCipherProvider("keyName",
                                             KeyStore.getInstance("AndroidKeyStore").apply { load(null) },
                                             "AndroidKeyStore"))

    private val app get() = ApplicationProvider.getApplicationContext<App>()
    private val vrtnuTokenStore = VRTTokenStoreImpl(app, crypto)
    private val vtmgoTokenStore = VTMTokenStoreImpl(app, crypto)
    private val vierTokenStore = VIERTokenStoreImpl(app, crypto)

    private val encryptedDataStore
        get() = CompositeTokenStorage(
            vrtnuTokenStore,
            vtmgoTokenStore,
            vierTokenStore,
        )

    @Test
    fun shouldReturnFalseIfNoCredentialsArePresent() {
        runBlocking {
            encryptedDataStore.hasCredentialsForAtLeastOneBrand() shouldBe false
        }
    }

    @Test
    fun shouldReturnTrueIfHasAtLeastStoredOneCredential() {
        runBlocking {
            val string = Arb.string()
            val username = string.gen()
            val password = string.gen()
            vrtnuTokenStore.saveVRTCredentials(username, password)
            encryptedDataStore.hasCredentialsForAtLeastOneBrand() shouldBe true
        }
    }

    @Test
    fun shouldReturnFalseIfTokenIsNotExpiredForVRT() {
        runBlocking {
            vrtnuTokenStore.saveTokenWrapper(vrtTokenWrapperArb
                                                 .gen()
                                                 .copy(expiry = Expiry(System.currentTimeMillis() + ONE_HOUR)))
            val isVRTTokenExpired = encryptedDataStore.isTokenExpired(TokenStorage.Brand.VRT)
            isVRTTokenExpired shouldBe false
        }
    }

    @Test
    fun shouldReturnTrueIfTokenIsExpiredForVRT() {
        runBlocking {
            vrtnuTokenStore.saveTokenWrapper(vrtTokenWrapperArb
                                                 .gen()
                                                 .copy(expiry = Expiry(System.currentTimeMillis() - ONE_HOUR)))
            val isVRTTokenExpired = encryptedDataStore.isTokenExpired(TokenStorage.Brand.VRT)
            isVRTTokenExpired shouldBe true
        }
    }

    @Test
    fun shouldReturnFalseIfTokenIsNotExpiredForVTM() {
        runBlocking {
            vtmgoTokenStore.saveToken(vtmTokenWrapperArb
                                          .gen()
                                          .copy(expiry = be.tapped.vtmgo.profile.Expiry(System.currentTimeMillis() + ONE_HOUR)))
            val isVTMTokenExpired = encryptedDataStore.isTokenExpired(TokenStorage.Brand.VTM)
            isVTMTokenExpired shouldBe false
        }
    }

    @Test
    fun shouldReturnTrueIfTokenIsExpiredForVTM() {
        runBlocking {
            vtmgoTokenStore.saveToken(vtmTokenWrapperArb
                                          .gen()
                                          .copy(expiry = be.tapped.vtmgo.profile.Expiry(System.currentTimeMillis() - ONE_HOUR)))
            val isVTMTokenExpired = encryptedDataStore.isTokenExpired(TokenStorage.Brand.VTM)
            isVTMTokenExpired shouldBe true
        }
    }

    @Test
    fun shouldReturnFalseIfTokenIsNotExpiredForVIER() {
        runBlocking {
            vierTokenStore.saveToken(vierTokenArb
                                         .gen()
                                         .copy(expiry = be.tapped.vier.profile.Expiry(System.currentTimeMillis() + ONE_HOUR)))
            val isVIERTokenExpired = encryptedDataStore.isTokenExpired(TokenStorage.Brand.VIER)
            isVIERTokenExpired shouldBe false
        }
    }

    @Test
    fun shouldReturnTrueIfTokenIsExpiredForVIER() {
        runBlocking {
            vierTokenStore.saveToken(vierTokenArb
                                         .gen()
                                         .copy(expiry = be.tapped.vier.profile.Expiry(System.currentTimeMillis() - ONE_HOUR)))
            val isVIERTokenExpired = encryptedDataStore.isTokenExpired(TokenStorage.Brand.VIER)
            isVIERTokenExpired shouldBe true
        }
    }
}
