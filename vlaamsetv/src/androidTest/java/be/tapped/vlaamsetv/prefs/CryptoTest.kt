package be.tapped.vlaamsetv.prefs

import androidx.test.ext.junit.runners.AndroidJUnit4
import be.tapped.vlaamsetv.gen
import be.tapped.vlaamsetv.prefs.vrt.TokenWrapper
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyStore

@RunWith(AndroidJUnit4::class)
internal class CryptoTest {

    private val keyStoreName = "AndroidKeyStore"
    private val keyName = "vrt-nu"
    private val keyStore = KeyStore.getInstance(keyStoreName).apply { load(null) }

    private val aesCipherProvider = AesCipherProvider(keyName, keyStore, keyStoreName)
    private val crypto = CryptoImpl(aesCipherProvider)

    @Test
    fun simpleEncryptionAndDecryption() {
        val outputStream = ByteArrayOutputStream()
        crypto.encrypt("Hello World".toByteArray(), outputStream)
        val string = String(crypto.decrypt(ByteArrayInputStream(outputStream.toByteArray())))

        string shouldBe "Hello World"
    }

    @Test
    fun longerTextEncryptionAndDecryption() {
        val outputStream = ByteArrayOutputStream()
        crypto.encrypt(
            "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQeyJhdWQiOiJ2cnRudS1zaXRlIiwic3ViIjoiNmRlNjg1MjctNGVjMi00MmUwLTg0YmEtNGU5ZjE3ZTQ4MmY2IiwiaXNzIjoiaHR0cHM6XC9cL2xvZ2luLnZydC5iZSIsInNjb3BlcyI6ImFkZHJlc3Msb3BlbmlkLHByb2ZpbGUsbGVnYWN5aWQsbWlkLGVtYW".toByteArray(),
            outputStream
        )
        val string = String(crypto.decrypt(ByteArrayInputStream(outputStream.toByteArray())))

        string shouldBe "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQeyJhdWQiOiJ2cnRudS1zaXRlIiwic3ViIjoiNmRlNjg1MjctNGVjMi00MmUwLTg0YmEtNGU5ZjE3ZTQ4MmY2IiwiaXNzIjoiaHR0cHM6XC9cL2xvZ2luLnZydC5iZSIsInNjb3BlcyI6ImFkZHJlc3Msb3BlbmlkLHByb2ZpbGUsbGVnYWN5aWQsbWlkLGVtYW"
    }

    @Test
    fun tokenWrapperProtoEncryptionAndDecryption() {
        val outputStream = ByteArrayOutputStream()
        val tokenWrapper =
            TokenWrapper(accessToken = Arb.string().gen())
        crypto.encrypt(
            TokenWrapper.ADAPTER.encode(tokenWrapper),
            outputStream
        )

        val tokenWrapperProto =
            TokenWrapper.ADAPTER.decode(crypto.decrypt(ByteArrayInputStream(outputStream.toByteArray())))
        tokenWrapper shouldBe tokenWrapperProto
    }
}

