package be.tapped.vlaamsetv.prefs.vier

import android.content.Context
import androidx.datastore.createDataStore
import be.tapped.vier.ApiResponse
import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.vrt.VRTNUCredentialsSerializer

interface VIERTokenStore {
    suspend fun saveVierCredentials(username: String, password: String)
    suspend fun token(): ApiResponse.Success.Authentication.Token
    suspend fun saveTokenWrapper(token: ApiResponse.Success.Authentication.Token)
}

class VIERTokenStoreImpl(context: Context, crypto: Crypto) : VIERTokenStore {

    private val vierTokenDataStore by lazy {
        context.createDataStore(
            fileName = "vier-token.pb",
            serializer = TokenSerializer(crypto)
        )
    }

    private val credentialsDataStore by lazy {
        context.createDataStore(
            fileName = "vier-credentials.pb",
            serializer = VRTNUCredentialsSerializer(crypto)
        )
    }

    override suspend fun saveVierCredentials(username: String, password: String) {
        credentialsDataStore.updateData {
            it.copy(username = username, password = password)
        }
    }

    override suspend fun token(): ApiResponse.Success.Authentication.Token {
        TODO("Not yet implemented")
    }

    override suspend fun saveTokenWrapper(token: ApiResponse.Success.Authentication.Token) {
        vierTokenDataStore.updateData {
            it.copy(
                accessToken = token.accessToken.token,
                expiresIn = token.expiresIn,
                tokenType = token.tokenType,
                refreshToken = token.refreshToken.token,
                idToken = token.idToken.token,
            )
        }
    }
}
