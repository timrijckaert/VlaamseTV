package be.tapped.vlaamsetv.auth.prefs

import arrow.fx.coroutines.parTupledN
import be.tapped.vlaamsetv.auth.prefs.vier.VIERTokenStore
import be.tapped.vlaamsetv.auth.prefs.vrt.VRTTokenStore
import be.tapped.vlaamsetv.auth.prefs.vtm.VTMTokenStore

interface TokenStorage {
    enum class Brand {
        VRT,
        VTM,
        VIER,
    }

    suspend fun hasCredentialsForAtLeastOneBrand(): Boolean

    suspend fun isTokenExpired(brand: Brand): Boolean
}

class CompositeTokenStorage(
    private val vrtTokenStore: VRTTokenStore,
    private val vtmTokenStore: VTMTokenStore,
    private val vierTokenStore: VIERTokenStore,
) : TokenStorage {

    override suspend fun hasCredentialsForAtLeastOneBrand(): Boolean {
        val (hasVrtCredentials, hasVtmCredentials, hasVierCredentials) = parTupledN({ vrtTokenStore.vrtCredentials() != null },
            { vtmTokenStore.vtmCredentials() != null },
            { vierTokenStore.vierCredentials() != null })
        return hasVrtCredentials || hasVtmCredentials || hasVierCredentials
    }

    override suspend fun isTokenExpired(brand: TokenStorage.Brand): Boolean {
        fun isExpired(expireDateInMillis: Long?): Boolean = expireDateInMillis ?: -1 <= System.currentTimeMillis()

        val expiryInMillis = when (brand) {
            TokenStorage.Brand.VRT -> vrtTokenStore.token()?.expiry?.dateInMillis
            TokenStorage.Brand.VTM -> vtmTokenStore.token()?.expiry?.dateInMillis
            TokenStorage.Brand.VIER -> vierTokenStore.token()?.expiry?.dateInMillis
        }
        return isExpired(expiryInMillis)
    }

}
