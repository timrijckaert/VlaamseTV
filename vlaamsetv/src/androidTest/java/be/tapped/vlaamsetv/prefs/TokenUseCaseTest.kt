package be.tapped.vlaamsetv.prefs

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.gen
import be.tapped.vlaamsetv.prefs.vier.VIERTokenStoreImpl
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStoreImpl
import be.tapped.vlaamsetv.prefs.vtm.VTMTokenStoreImpl
import be.tapped.vlaamsetv.vrtTokenWrapperArb
import be.tapped.vlaamsetv.vtmTokenWrapper
import be.tapped.vrtnu.profile.Expiry
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyStore

@RunWith(AndroidJUnit4::class)
internal class TokenUseCaseTest {

    private val crypto
        get() = CryptoImpl(
            AesCipherProvider(
                "keyName",
                KeyStore.getInstance("AndroidKeyStore").apply { load(null) },
                "AndroidKeyStore"
            )
        )

    private val app get() = ApplicationProvider.getApplicationContext<App>()
    private val vrtnuTokenStore = VRTTokenStoreImpl(app, crypto)
    private val vtmgoTokenStore = VTMTokenStoreImpl(app, crypto)
    private val vierTokenStore = VIERTokenStoreImpl(app, crypto)

    private val encryptedDataStore
        get() = CompositeTokenCollectorUseCase(
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
            vrtnuTokenStore.saveTokenWrapper(
                vrtTokenWrapperArb.gen()
                    .copy(expiry = Expiry(System.currentTimeMillis() + 1 * 60 * 60 * 1000))
            )
            val isVRTTokenExpired = encryptedDataStore.isTokenExpired(TokenUseCase.Brand.VRT)
            isVRTTokenExpired shouldBe false
        }
    }

    @Test
    fun shouldReturnTrueIfTokenIsExpiredForVRT() {
        runBlocking {
            vrtnuTokenStore.saveTokenWrapper(
                vrtTokenWrapperArb.gen()
                    .copy(expiry = Expiry(System.currentTimeMillis() - 1 * 60 * 60 * 1000))
            )
            val isVRTTokenExpired = encryptedDataStore.isTokenExpired(TokenUseCase.Brand.VRT)
            isVRTTokenExpired shouldBe true
        }
    }

    @Test
    fun shouldReturnFalseIfTokenIsExpiredForVTM() {
        runBlocking {
            vtmgoTokenStore.saveToken(
                vtmTokenWrapper.gen()
                    .copy(expiry = be.tapped.vtmgo.profile.Expiry(System.currentTimeMillis() + 1 * 60 * 60 * 1000))
            )
            val isVTMTokenExpired = encryptedDataStore.isTokenExpired(TokenUseCase.Brand.VTM)
            isVTMTokenExpired shouldBe false
        }
    }

    @Test
    fun shouldReturnTrueIfTokenIsExpiredForVTM() {
        runBlocking {
            vtmgoTokenStore.saveToken(
                vtmTokenWrapper.gen()
                    .copy(expiry = be.tapped.vtmgo.profile.Expiry(System.currentTimeMillis() - 1 * 60 * 60 * 1000))
            )
            val isVTMTokenExpired = encryptedDataStore.isTokenExpired(TokenUseCase.Brand.VTM)
            isVTMTokenExpired shouldBe true
        }
    }
}
