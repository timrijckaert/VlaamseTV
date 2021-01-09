package be.tapped.vlaamsetv.prefs

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyStore

@RunWith(AndroidJUnit4::class)
internal class CryptoTest {

    @Test
    fun encryptionAndDecryption() {
        val keyStoreName = "AndroidKeyStore"
        val keyName = "vrt-nu"
        val keyStore = KeyStore.getInstance(keyStoreName)
        keyStore.load(null)

        val aesCipherProvider = AesCipherProvider(keyName, keyStore, keyStoreName)
        val crypto = CryptoImpl(aesCipherProvider)

        val outputStream = ByteArrayOutputStream()
        crypto.encrypt("Hello World".toByteArray(), outputStream)
        val string = String(crypto.decrypt(ByteArrayInputStream(outputStream.toByteArray())))

        assertThat(string, `is`("Hello World"))
    }
}
