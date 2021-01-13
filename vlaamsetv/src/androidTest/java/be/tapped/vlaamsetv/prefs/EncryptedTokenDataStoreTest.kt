package be.tapped.vlaamsetv.prefs

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.gen
import be.tapped.vlaamsetv.tokenWrapperArb
import be.tapped.vrtnu.profile.AccessToken
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyStore

@RunWith(AndroidJUnit4::class)
internal class EncryptedTokenDataStoreTest {

    private val cryptoImpl
        get() = CryptoImpl(
            AesCipherProvider(
                "keyName",
                KeyStore.getInstance(App.KEYSTORE_NAME).apply { load(null) },
                App.KEYSTORE_NAME
            )
        )

    private val encryptedDataStore
        get() = EncryptedTokenDataStore(
            ApplicationProvider.getApplicationContext(),
            cryptoImpl
        )

    @Test
    fun nothingInsideTheDataStoreShouldReturnNull() {
        runBlocking {
            encryptedDataStore.tokenWrapper() shouldBe null
        }
    }

    @Test
    fun savingAVRTTokenWrapperShouldBeRetrievable() {
        runBlocking {
            val tokenWrapper = tokenWrapperArb.gen()
            encryptedDataStore.saveTokenWrapper(tokenWrapper)
            encryptedDataStore.tokenWrapper() shouldBe tokenWrapper
        }
    }

    @Test
    fun savingALongToken() {
        runBlocking {
            val tokenWrapper = tokenWrapperArb.gen()
                .copy(accessToken = AccessToken("eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQeyJhdWQiOiJ2cnRudS1zaXRlIiwic3ViIjoiNmRlNjg1MjctNGVjMi00MmUwLTg0YmEtNGU5ZjE3ZTQ4MmY2IiwiaXNzIjoiaHR0cHM6XC9cL2xvZ2luLnZydC5iZSIsInNjb3Blct"))

            encryptedDataStore.saveTokenWrapper(tokenWrapper)
            encryptedDataStore.tokenWrapper() shouldBe tokenWrapper
        }
    }

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
            encryptedDataStore.saveVRTCredentials(username, password)
            encryptedDataStore.hasCredentialsForAtLeastOneBrand() shouldBe true
        }
    }
}