package be.tapped.vlaamsetv.auth.prefs

import arrow.fx.coroutines.parZip
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

    override suspend fun hasCredentialsForAtLeastOneBrand(): Boolean =
        parZip(
            { vrtTokenStore.vrtCredentials() != null },
            { vtmTokenStore.vtmCredentials() != null },
            { goPlayTokenStore.goPlayCredentials() != null }
        ) { hasVrtCredentials, hasVtmCredentials, hasGoPlayCredentials -> hasVrtCredentials || hasVtmCredentials || hasGoPlayCredentials }

    override suspend fun isTokenExpired(brand: TokenStorage.Brand): Boolean {
        fun isExpired(expireDateInMillis: Long?): Boolean = expireDateInMillis ?: -1 <= System.currentTimeMillis()

        return when (brand) {
            TokenStorage.Brand.VRT -> isExpired(vrtTokenStore.token()?.expiry?.dateInMillis)
            TokenStorage.Brand.VTM -> false // TODO Check VTM does not has an
            TokenStorage.Brand.GoPlay -> isExpired(goPlayTokenStore.token()?.expiry?.dateInMillis)
        }
    }

}
