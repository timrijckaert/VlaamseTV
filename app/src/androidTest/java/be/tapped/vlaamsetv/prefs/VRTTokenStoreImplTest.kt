package be.tapped.vlaamsetv.prefs

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.auth.prefs.Credential
import be.tapped.vlaamsetv.auth.prefs.vrt.VRTTokenStoreImpl
import be.tapped.vlaamsetv.gen
import be.tapped.vlaamsetv.vrtTokenWrapperArb
import be.tapped.vrtnu.profile.AccessToken
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyStore

@RunWith(AndroidJUnit4::class)
class VRTTokenStoreImplTest {

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

    @Test
    fun nothingInsideTheDataStoreShouldReturnANullVRTToken() {
        runBlocking {
            vrtnuTokenStore.token() shouldBe null
        }
    }

    @Test
    fun savingAVRTTokenShouldAndRetrievingResultsInTheSameObject() {
        runBlocking {
            val tokenWrapper = vrtTokenWrapperArb.gen()
            vrtnuTokenStore.saveTokenWrapper(tokenWrapper)
            vrtnuTokenStore.token() shouldBe tokenWrapper
        }
    }

    // This test is here since this behavior broke before.
    @Test
    fun savingALongToken() {
        runBlocking {
            val tokenWrapper = vrtTokenWrapperArb
                .gen()
                .copy(accessToken = AccessToken("eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQeyJhdWQiOiJ2cnRudS1zaXRlIiwic3ViIjoiNmRlNjg1MjctNGVjMi00MmUwLTg0YmEtNGU5ZjE3ZTQ4MmY2IiwiaXNzIjoiaHR0cHM6XC9cL2xvZ2luLnZydC5iZSIsInNjb3Blct"))

            vrtnuTokenStore.saveTokenWrapper(tokenWrapper)
            vrtnuTokenStore.token() shouldBe tokenWrapper
        }
    }

    @Test
    fun savingCredentialsShouldReturnTheSameObject() {
        runBlocking {
            vrtnuTokenStore.vrtCredentials() shouldBe null
            val stringGen = Arb.string(1)
            val username = stringGen.gen()
            val password = stringGen.gen()
            vrtnuTokenStore.saveVRTCredentials(username, password)
            vrtnuTokenStore.vrtCredentials() shouldBe Credential(username, password)
        }
    }
}
