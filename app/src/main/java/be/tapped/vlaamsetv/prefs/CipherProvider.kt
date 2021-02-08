package be.tapped.vlaamsetv.prefs

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

interface CipherProvider {

    val encryptCipher: Cipher
    fun decryptCipher(iv: ByteArray): Cipher
}

class AesCipherProvider(private val keyName: String, private val keyStore: KeyStore, private val keyStoreName: String) :
    CipherProvider {

    override val encryptCipher: Cipher
        get() = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }

    override fun decryptCipher(iv: ByteArray): Cipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.DECRYPT_MODE, getOrCreateKey(), IvParameterSpec(iv))
    }

    private fun getOrCreateKey(): SecretKey =
        (keyStore.getEntry(keyName, null) as? KeyStore.SecretKeyEntry)?.secretKey ?: generateKey()

    private fun generateKey(): SecretKey =
        KeyGenerator.getInstance(ALGORITHM, keyStoreName).apply { init(keyGenParams) }.generateKey()

    private val keyGenParams =
        KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).apply {
            setBlockModes(BLOCK_MODE)
            setEncryptionPaddings(PADDING)
            setUserAuthenticationRequired(false)
            setRandomizedEncryptionRequired(true)
        }.build()

    private companion object {

        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}
