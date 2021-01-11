package be.tapped.vlaamsetv.prefs

import android.content.Context
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStore
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStoreImpl
import be.tapped.vlaamsetv.prefs.vtm.VTMTokenStore
import be.tapped.vlaamsetv.prefs.vtm.VTMTokenStoreImpl

class EncryptedDataStore(
    private val context: Context,
    private val crypto: Crypto,
    private val vrtTokenStore: VRTTokenStore = VRTTokenStoreImpl(context, crypto),
    private val vtmTokenStore: VTMTokenStore = VTMTokenStoreImpl(context, crypto)
) :
    VRTTokenStore by vrtTokenStore,
    VTMTokenStore by vtmTokenStore
