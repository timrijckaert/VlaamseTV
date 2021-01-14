package be.tapped.vlaamsetv.prefs

import arrow.fx.coroutines.parTupledN
import be.tapped.vlaamsetv.prefs.vier.VIERTokenStore
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStore
import be.tapped.vlaamsetv.prefs.vtm.VTMTokenStore

interface TokenUseCase {
    enum class Brand {
        VRT,
        VTM,
        VIER,
    }

    suspend fun hasCredentialsForAtLeastOneBrand(): Boolean

    suspend fun isTokenExpired(brand: Brand): Boolean
}

class CompositeTokenCollectorUseCase(
    private val vrtTokenStore: VRTTokenStore,
    private val vtmTokenStore: VTMTokenStore,
    private val vierTokenStore: VIERTokenStore,
) : TokenUseCase {

    override suspend fun hasCredentialsForAtLeastOneBrand(): Boolean {
        val (hasVrtCredentials, hasVtmCredentials, hasVierCredentials) = parTupledN(
            { vrtTokenStore.vrtCredentials() != null },
            { vtmTokenStore.vtmCredentials() != null },
            { vierTokenStore.vierCredentials() != null }
        )
        return hasVrtCredentials || hasVtmCredentials || hasVierCredentials
    }

    override suspend fun isTokenExpired(brand: TokenUseCase.Brand): Boolean {
        fun isExpired(expireDateInMillis: Long?): Boolean =
            expireDateInMillis ?: -1 <= System.currentTimeMillis()

        val expiryInMillis = when (brand) {
            TokenUseCase.Brand.VRT -> vrtTokenStore.token()?.expiry?.dateInMillis
            TokenUseCase.Brand.VTM -> vtmTokenStore.token()?.expiry?.dateInMillis
            TokenUseCase.Brand.VIER -> vierTokenStore.token()?.expiry?.dateInMillis
        }
        return isExpired(expiryInMillis)
    }

}
