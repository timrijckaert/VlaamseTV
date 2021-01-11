package be.tapped.vlaamsetv.prefs.vrt

import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.EncryptedProtoSerializer

class TokenWrapperProtoSerializer(crypto: Crypto) :
    EncryptedProtoSerializer<TokenWrapperProto>(
        crypto,
        TokenWrapperProto(),
        TokenWrapperProto.ADAPTER
    )

class VRTNUCredentialsSerializer(crypto: Crypto) :
    EncryptedProtoSerializer<VRTNUCredentials>(
        crypto,
        VRTNUCredentials(),
        VRTNUCredentials.ADAPTER
    )
