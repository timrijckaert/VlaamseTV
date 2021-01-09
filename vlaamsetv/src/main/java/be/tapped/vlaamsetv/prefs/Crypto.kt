package be.tapped.vlaamsetv.prefs

import java.io.InputStream
import java.io.OutputStream

public interface Crypto {
    public fun encrypt(rawBytes: ByteArray, outputStream: OutputStream)
    public fun decrypt(inputStream: InputStream): ByteArray
}

internal class CryptoImpl constructor(private val cipherProvider: CipherProvider) : Crypto {

    override fun encrypt(rawBytes: ByteArray, outputStream: OutputStream) {
        val cipher = cipherProvider.encryptCipher
        val encryptedBytes = cipher.doFinal(rawBytes)
        with(outputStream) {
            write(cipher.iv.size)
            write(cipher.iv)
            write(encryptedBytes.size)
            write(encryptedBytes)
        }
    }

    override fun decrypt(inputStream: InputStream): ByteArray {
        val ivSize = inputStream.read()
        val iv = ByteArray(ivSize)
        inputStream.read(iv)
        val encryptedDataSize = inputStream.read()
        val encryptedData = ByteArray(encryptedDataSize)
        inputStream.read(encryptedData)
        val cipher = cipherProvider.decryptCipher(iv)
        return cipher.doFinal(encryptedData)
    }
}
