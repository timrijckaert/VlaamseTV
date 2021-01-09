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
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException
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
            override val defaultValue: TokenWrapperProto = TokenWrapperProto()

            override fun readFrom(input: InputStream): TokenWrapperProto {
                try {
                    return TokenWrapperProto.ADAPTER.decode(input)
                } catch (exception: IOException) {
                    throw CorruptionException("Cannot read proto.", exception)
                }
            }

            override fun writeTo(
                t: TokenWrapperProto,
                output: OutputStream
            ) = TokenWrapperProto.ADAPTER.encode(output, t)
        }
    }

    override suspend fun tokenWrapper(): TokenWrapper? =
        vrtNuDataStore.data.firstOrNull()?.let {
            if (it.access_token.isNotEmpty() && it.refresh_token.isNotEmpty() && it.expiry != 0L) {
                TokenWrapper(
                    accessToken = AccessToken(it.access_token),
                    refreshToken = RefreshToken(it.refresh_token),
                    expiry = Expiry(it.expiry),
                )
            } else {
                null
            }
        }

    override suspend fun saveTokenWrapper(tokenWrapper: TokenWrapper) {
        vrtNuDataStore.updateData {
            it.copy(
                access_token = tokenWrapper.accessToken.token,
                refresh_token = tokenWrapper.refreshToken.token,
                expiry = tokenWrapper.expiry.date
            )
        }
    }
}
