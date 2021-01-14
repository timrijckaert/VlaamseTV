package be.tapped.vlaamsetv.prefs.vier

import android.content.Context
import androidx.datastore.createDataStore
import be.tapped.vier.ApiResponse
import be.tapped.vier.profile.AccessToken
import be.tapped.vier.profile.Expiry
import be.tapped.vier.profile.IdToken
import be.tapped.vier.profile.RefreshToken
import be.tapped.vlaamsetv.prefs.Credential
import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.vrt.VRTNUCredentialsSerializer
import kotlinx.coroutines.flow.firstOrNull

interface VIERTokenStore {
    suspend fun saveVierCredentials(username: String, password: String)
    suspend fun vierCredentials(): Credential?
    suspend fun token(): ApiResponse.Success.Authentication.Token?
    suspend fun saveToken(token: ApiResponse.Success.Authentication.Token)
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

    override suspend fun vierCredentials(): Credential? =
        credentialsDataStore.data.firstOrNull()?.let {
            if (it.username.isNotBlank() && it.password.isNotBlank()) {
                Credential(
                    username = it.username,
                    password = it.password
                )
            } else {
                null
            }
        }

    override suspend fun token(): ApiResponse.Success.Authentication.Token? =
        vierTokenDataStore.data.firstOrNull()?.let {
            if (it.accessToken.isNotBlank() && it.expiresIn != 0L && it.tokenType.isNotBlank() && it.refreshToken.isNotBlank() && it.idToken.isNotBlank()) {
                ApiResponse.Success.Authentication.Token(
                    accessToken = AccessToken(it.accessToken),
                    expiry = Expiry(it.expiresIn),
                    tokenType = it.tokenType,
                    refreshToken = RefreshToken(it.refreshToken),
                    idToken = IdToken(it.idToken),
                )
            } else {
                null
            }
        }

    override suspend fun saveToken(token: ApiResponse.Success.Authentication.Token) {
        vierTokenDataStore.updateData {
            it.copy(
                accessToken = token.accessToken.token,
                expiresIn = token.expiry.dateInMillis,
                tokenType = token.tokenType,
                refreshToken = token.refreshToken.token,
                idToken = token.idToken.token,
            )
        }
    }
}
