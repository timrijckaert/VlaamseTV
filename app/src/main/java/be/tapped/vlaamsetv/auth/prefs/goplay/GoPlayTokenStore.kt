package be.tapped.vlaamsetv.auth.prefs.goplay

import android.content.Context
import androidx.datastore.createDataStore
import be.tapped.goplay.profile.AccessToken
import be.tapped.goplay.profile.Expiry
import be.tapped.goplay.profile.IdToken
import be.tapped.goplay.profile.RefreshToken
import be.tapped.goplay.profile.TokenWrapper
import be.tapped.vlaamsetv.auth.prefs.Credential
import be.tapped.vlaamsetv.prefs.Crypto
import kotlinx.coroutines.flow.firstOrNull

interface GoPlayTokenStore {

    suspend fun saveGoPlayCredentials(username: String, password: String)
    suspend fun goPlayCredentials(): Credential?
    suspend fun token(): TokenWrapper?
    suspend fun saveToken(token: TokenWrapper)
}

class GoPlayTokenStoreImpl(context: Context, crypto: Crypto) : GoPlayTokenStore {

    private val goPlayTokenDataStore by lazy {
        context.createDataStore(fileName = "go-play-token.pb", serializer = TokenSerializer(crypto))
    }

    private val credentialsDataStore by lazy {
        context.createDataStore(fileName = "go-play-credentials.pb", serializer = GoPlayCredentialsSerializer(crypto))
    }

    override suspend fun saveGoPlayCredentials(username: String, password: String) {
        credentialsDataStore.updateData {
            it.copy(username = username, password = password)
        }
    }

    override suspend fun goPlayCredentials(): Credential? =
        credentialsDataStore.data.firstOrNull()?.let {
            if (it.username.isNotBlank() && it.password.isNotBlank()) {
                Credential(username = it.username, password = it.password)
            } else {
                null
            }
        }

    override suspend fun token(): TokenWrapper? = goPlayTokenDataStore.data.firstOrNull()?.let {
        if (it.accessToken.isNotBlank() && it.expiresIn != 0L && it.tokenType.isNotBlank() && it.refreshToken.isNotBlank() && it.idToken.isNotBlank()) {
            TokenWrapper(
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

    override suspend fun saveToken(token: TokenWrapper) {
        goPlayTokenDataStore.updateData {
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
