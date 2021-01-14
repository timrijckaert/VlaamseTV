package be.tapped.vlaamsetv.prefs.vtm

import android.content.Context
import androidx.datastore.createDataStore
import be.tapped.vlaamsetv.prefs.Credential
import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vtmgo.profile.Expiry
import be.tapped.vtmgo.profile.JWT
import be.tapped.vtmgo.profile.TokenWrapper
import kotlinx.coroutines.flow.firstOrNull

interface VTMTokenStore {
    suspend fun token(): TokenWrapper?
    suspend fun saveToken(token: TokenWrapper)
    suspend fun saveVTMCredentials(username: String, password: String)
    suspend fun vtmCredentials(): Credential?
}

class VTMTokenStoreImpl(context: Context, crypto: Crypto) : VTMTokenStore {

    private val jwtTokenDataStore by lazy {
        context.createDataStore(
            fileName = "vtmgo-jwt.pb",
            serializer = TokenWrapperSerializer(crypto)
        )
    }

    private val credentialsDataStore by lazy {
        context.createDataStore(
            fileName = "vtmgo-credentials.pb",
            serializer = VTMCredentialsSerializer(crypto)
        )
    }

    override suspend fun token(): TokenWrapper? =
        jwtTokenDataStore.data.firstOrNull()?.let {
            if (it.token.isNotBlank() && it.expiry != 0L) {
                TokenWrapper(
                    JWT(it.token),
                    Expiry(it.expiry)
                )
            } else {
                null
            }
        }

    override suspend fun saveToken(token: TokenWrapper) {
        jwtTokenDataStore.updateData {
            it.copy(
                token = token.jwt.token,
                expiry = token.expiry.dateInMillis
            )
        }
    }

    override suspend fun saveVTMCredentials(username: String, password: String) {
        credentialsDataStore.updateData {
            it.copy(username = username, password = password)
        }
    }

    override suspend fun vtmCredentials(): Credential? =
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
}
