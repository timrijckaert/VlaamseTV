package be.tapped.vlaamsetv.prefs

import android.content.Context
import be.tapped.vlaamsetv.prefs.vier.VIERTokenStore
import be.tapped.vlaamsetv.prefs.vier.VIERTokenStoreImpl
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStore
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStoreImpl
import be.tapped.vlaamsetv.prefs.vtm.VTMTokenStore
import be.tapped.vlaamsetv.prefs.vtm.VTMTokenStoreImpl

class EncryptedTokenDataStore(
    private val context: Context,
    private val crypto: Crypto,
    private val vrtTokenStore: VRTTokenStore = VRTTokenStoreImpl(context, crypto),
    private val vtmTokenStore: VTMTokenStore = VTMTokenStoreImpl(context, crypto),
    private val vierTokenStore: VIERTokenStore = VIERTokenStoreImpl(context, crypto),
) :
    VRTTokenStore by vrtTokenStore,
    VTMTokenStore by vtmTokenStore,
    VIERTokenStore by vierTokenStore
