package be.tapped.vlaamsetv.prefs.vrt

import android.content.Context
import androidx.datastore.createDataStore
import be.tapped.vlaamsetv.prefs.Credential
import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vrtnu.profile.*
import be.tapped.vrtnu.profile.TokenWrapper
import be.tapped.vrtnu.profile.XVRTToken
import kotlinx.coroutines.flow.firstOrNull

interface VRTTokenStore {
    suspend fun saveVRTCredentials(username: String, password: String)
    suspend fun vrtCredentials(): Credential?
    suspend fun tokenWrapper(): TokenWrapper?
    suspend fun saveTokenWrapper(tokenWrapper: TokenWrapper)
    suspend fun saveXVRTToken(xVRTToken: XVRTToken)
    suspend fun xVRTToken(): XVRTToken?
}

class VRTTokenStoreImpl(context: Context, crypto: Crypto) : VRTTokenStore {
    private val vrtnuTokenDataStore by lazy {
        context.createDataStore(
            fileName = "vrtnu-token.pb",
            serializer = TokenWrapperSerializer(crypto)
        )
    }

    private val credentialsDataStore by lazy {
        context.createDataStore(
            fileName = "vrtnu-credentials.pb",
            serializer = VRTNUCredentialsSerializer(crypto)
        )
    }

    private val xVRTDataStore by lazy {
        context.createDataStore(
            fileName = "vrtnu-xvrt.pb",
            serializer = XVRTTokenSerializer(crypto)
        )
    }

    override suspend fun saveVRTCredentials(username: String, password: String) {
        credentialsDataStore.updateData {
            it.copy(username = username, password = password)
        }
    }

    override suspend fun vrtCredentials(): Credential? =
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

    override suspend fun tokenWrapper(): TokenWrapper? =
        vrtnuTokenDataStore.data.firstOrNull()?.let {
            if (it.accessToken.isNotBlank() && it.refreshToken.isNotBlank() && it.expiry != 0L) {
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
        vrtnuTokenDataStore.updateData {
            it.copy(
                accessToken = tokenWrapper.accessToken.token,
                refreshToken = tokenWrapper.refreshToken.token,
                expiry = tokenWrapper.expiry.date
            )
        }
    }

    override suspend fun saveXVRTToken(xVRTToken: XVRTToken) {
        xVRTDataStore.updateData { it.copy(token = xVRTToken.token) }
    }

    override suspend fun xVRTToken(): XVRTToken? =
        xVRTDataStore.data.firstOrNull()?.let {
            if (it.token.isNotEmpty()) {
                XVRTToken(it.token)
            } else {
                null
            }
        }
}
