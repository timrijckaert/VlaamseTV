package be.tapped.vlaamsetv.prefs

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.tapped.vlaamsetv.App
import be.tapped.vlaamsetv.gen
import be.tapped.vlaamsetv.tokenWrapperArb
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyStore

@RunWith(AndroidJUnit4::class)
internal class EncryptedDataStoreTest {

    private val encryptedDataStore = EncryptedDataStore(
        ApplicationProvider.getApplicationContext(),
        CryptoImpl(
            AesCipherProvider(
                "keyName",
                KeyStore.getInstance(App.KEYSTORE_NAME).apply { load(null) },
                App.KEYSTORE_NAME
            )
        )
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
}
