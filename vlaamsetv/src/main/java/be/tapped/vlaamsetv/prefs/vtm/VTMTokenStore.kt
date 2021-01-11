package be.tapped.vlaamsetv.prefs.vtm

import android.content.Context
import androidx.datastore.createDataStore
import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.profile.JWT
import kotlinx.coroutines.flow.firstOrNull

interface VTMTokenStore {
    suspend fun jwt(): JWT?
    suspend fun saveJWT(jwt: ApiResponse.Success.Authentication.Token)
}

class VTMTokenStoreImpl(context: Context, crypto: Crypto) : VTMTokenStore {

    private val jwtTokenDataStore by lazy {
        context.createDataStore(
            fileName = "vtmgo-jwt.pb",
            serializer = JWTProtoSerializer(crypto)
        )
    }

    override suspend fun jwt(): JWT? =
        jwtTokenDataStore.data.firstOrNull()?.let {
            if (it.token.isNotBlank()) {
                JWT(it.token)
            } else {
                null
            }
        }

    override suspend fun saveJWT(jwt: ApiResponse.Success.Authentication.Token) {
        jwtTokenDataStore.updateData { it.copy(token = jwt.jwt.token) }
    }
}
