package be.tapped.vlaamsetv.prefs.vrt

import android.content.Context
import androidx.datastore.createDataStore
import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vrtnu.profile.AccessToken
import be.tapped.vrtnu.profile.Expiry
import be.tapped.vrtnu.profile.RefreshToken
import be.tapped.vrtnu.profile.TokenWrapper
import kotlinx.coroutines.flow.firstOrNull

interface VRTTokenStore {
    suspend fun saveCredentials(username: String, password: String)
    suspend fun tokenWrapper(): TokenWrapper?
    suspend fun saveTokenWrapper(tokenWrapper: TokenWrapper)
}

class VRTTokenStoreImpl(context: Context, crypto: Crypto) : VRTTokenStore {
    private val vrtnuTokenDataStore by lazy {
        context.createDataStore(
            fileName = "vrtnu-token.pb",
            serializer = TokenWrapperProtoSerializer(crypto)
        )
    }

    private val credentialsDataStore by lazy {
        context.createDataStore(
            fileName = "vrtnu-credentials.pb",
            serializer = VRTNUCredentialsSerializer(crypto)
        )
    }

    override suspend fun saveCredentials(username: String, password: String) {
        credentialsDataStore.updateData {
            it.copy(user_name = username, password = password)
        }
    }

    override suspend fun tokenWrapper(): TokenWrapper? =
        vrtnuTokenDataStore.data.firstOrNull()?.let {
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
        vrtnuTokenDataStore.updateData {
            it.copy(
                access_token = tokenWrapper.accessToken.token,
                refresh_token = tokenWrapper.refreshToken.token,
                expiry = tokenWrapper.expiry.date
            )
        }
    }
}
