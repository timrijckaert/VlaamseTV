package be.tapped.vlaamsetv.auth.prefs

import arrow.fx.coroutines.parMapN
import be.tapped.vlaamsetv.auth.prefs.goplay.GoPlayTokenStore
import be.tapped.vlaamsetv.auth.prefs.vrt.VRTTokenStore
import be.tapped.vlaamsetv.auth.prefs.vtm.VTMTokenStore

interface TokenStorage {
    enum class Brand {
        VRT,
        VTM,
        GoPlay,
    }

    suspend fun hasCredentialsForAtLeastOneBrand(): Boolean

    suspend fun isTokenExpired(brand: Brand): Boolean
}

class CompositeTokenStorage(
    private val vrtTokenStore: VRTTokenStore,
    private val vtmTokenStore: VTMTokenStore,
    private val goPlayTokenStore: GoPlayTokenStore,
) : TokenStorage {

    override suspend fun hasCredentialsForAtLeastOneBrand(): Boolean {
        val (hasVrtCredentials, hasVtmCredentials, hasGoPlayCredentials) = parMapN(
            { vrtTokenStore.vrtCredentials() != null },
            { vtmTokenStore.vtmCredentials() != null },
            { goPlayTokenStore.goPlayCredentials() != null },
            ::Triple
        )
        return hasVrtCredentials || hasVtmCredentials || hasGoPlayCredentials
    }

    override suspend fun isTokenExpired(brand: TokenStorage.Brand): Boolean {
        fun isExpired(expireDateInMillis: Long?): Boolean = expireDateInMillis ?: -1 <= System.currentTimeMillis()

        val expiryInMillis = when (brand) {
            TokenStorage.Brand.VRT -> vrtTokenStore.token()?.expiry?.dateInMillis
            TokenStorage.Brand.VTM -> vtmTokenStore.token()?.expiry?.dateInMillis
            TokenStorage.Brand.GoPlay -> goPlayTokenStore.token()?.expiry?.dateInMillis
        }
        return isExpired(expiryInMillis)
    }

}
