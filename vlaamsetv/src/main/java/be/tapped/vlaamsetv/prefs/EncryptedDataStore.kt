package be.tapped.vlaamsetv.prefs

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.createDataStore
import be.tapped.vlaamsetv.auth.TokenWrapperProto
import be.tapped.vrtnu.profile.AccessToken
import be.tapped.vrtnu.profile.Expiry
import be.tapped.vrtnu.profile.RefreshToken
import be.tapped.vrtnu.profile.TokenWrapper
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.firstOrNull
import java.io.InputStream
import java.io.OutputStream

public interface VRTTokenStore {
    public suspend fun tokenWrapper(): TokenWrapper?
    public suspend fun saveTokenWrapper(tokenWrapper: TokenWrapper)
}

public class EncryptedDataStore(private val context: Context, private val crypto: Crypto) :
    VRTTokenStore {

    private val vrtNuDataStore by lazy {
        context.createDataStore(
            fileName = "vrtnu.pb",
            serializer = vrtNuTokenWrapperSerializer
        )
    }

    private val vrtNuTokenWrapperSerializer by lazy {
        object : Serializer<TokenWrapperProto> {
            override val defaultValue: TokenWrapperProto = TokenWrapperProto.getDefaultInstance()

            override fun readFrom(input: InputStream): TokenWrapperProto {
                try {
                    return TokenWrapperProto.parseFrom(crypto.decrypt(input))
                } catch (exception: InvalidProtocolBufferException) {
                    throw CorruptionException("Cannot read proto.", exception)
                }
            }

            override fun writeTo(
                t: TokenWrapperProto,
                output: OutputStream
            ) = crypto.encrypt(t.toByteArray(), output)
        }
    }

    override suspend fun tokenWrapper(): TokenWrapper? =
        vrtNuDataStore.data.firstOrNull()?.let {
            if (!it.accessToken.isNullOrEmpty() && !it.refreshToken.isNullOrEmpty() && it.expiry != 0L) {
                TokenWrapper(
                    accessToken = AccessToken(it.accessToken),
                    refreshToken = RefreshToken(it.refreshToken),
                    expiry = Expiry(it.expiry),
                )
            } else {
                null
            }
        }

    override suspend fun saveTokenWrapper(tokenWrapper: TokenWrapper) {
        vrtNuDataStore.updateData {
            it.toBuilder()
                .setAccessToken(tokenWrapper.accessToken.token)
                .setRefreshToken(tokenWrapper.refreshToken.token)
                .setExpiry(tokenWrapper.expiry.date)
                .build()
        }
    }
}
