package be.tapped.vlaamsetv.prefs

import arrow.fx.coroutines.parTupledN
import be.tapped.vlaamsetv.prefs.vier.VIERTokenStore
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStore
import be.tapped.vlaamsetv.prefs.vtm.VTMTokenStore

interface TokenUseCase {
    suspend fun hasCredentialsForAtLeastOneBrand(): Boolean
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
}
