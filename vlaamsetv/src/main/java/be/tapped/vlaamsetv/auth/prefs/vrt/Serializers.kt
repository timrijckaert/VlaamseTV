package be.tapped.vlaamsetv.auth.prefs.vrt

import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.EncryptedProtoSerializer

class VRTTokenWrapperSerializer(crypto: Crypto) :
    EncryptedProtoSerializer<VRTTokenWrapper>(
        crypto,
        VRTTokenWrapper(),
        VRTTokenWrapper.ADAPTER
    )

class VRTNUCredentialsSerializer(crypto: Crypto) :
    EncryptedProtoSerializer<VRTNUCredentials>(
        crypto,
        VRTNUCredentials(),
        VRTNUCredentials.ADAPTER
    )

class XVRTTokenSerializer(crypto: Crypto) :
    EncryptedProtoSerializer<XVRTToken>(
        crypto,
        XVRTToken(),
        XVRTToken.ADAPTER
    )

