package be.tapped.vlaamsetv.prefs

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.squareup.wire.ProtoAdapter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

abstract class ProtoSerializer<T>(override val defaultValue: T, private val adapterT: ProtoAdapter<T>) : Serializer<T> {

    override fun readFrom(input: InputStream): T = if (input.available() != 0) {
        try {
            adapterT.decode(input)
        } catch (exception: IOException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    } else {
        defaultValue
    }

    override fun writeTo(t: T, output: OutputStream) {
        adapterT.encode(output, t)
    }
}

abstract class EncryptedProtoSerializer<T>(
    private val crypto: Crypto,
    override val defaultValue: T,
    private val adapterT: ProtoAdapter<T>,
) : Serializer<T> {

    override fun readFrom(input: InputStream): T = if (input.available() != 0) {
        try {
            adapterT.decode(crypto.decrypt(input))
        } catch (exception: IOException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    } else {
        defaultValue
    }

    override fun writeTo(t: T, output: OutputStream) {
        crypto.encrypt(adapterT.encode(t), output)
    }
}
