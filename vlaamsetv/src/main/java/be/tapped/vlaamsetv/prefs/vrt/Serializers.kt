package be.tapped.vlaamsetv.prefs.vrt

import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.EncryptedProtoSerializer

class TokenWrapperSerializer(crypto: Crypto) :
    EncryptedProtoSerializer<TokenWrapper>(
        crypto,
        TokenWrapper(),
        TokenWrapper.ADAPTER
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

