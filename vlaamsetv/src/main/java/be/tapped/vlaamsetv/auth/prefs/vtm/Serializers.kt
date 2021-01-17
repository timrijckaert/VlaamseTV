package be.tapped.vlaamsetv.auth.prefs.vtm

import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.EncryptedProtoSerializer

class TokenWrapperSerializer(crypto: Crypto) : EncryptedProtoSerializer<VTMTokenWrapper>(
    crypto,
    VTMTokenWrapper(),
    VTMTokenWrapper.ADAPTER
)

class VTMCredentialsSerializer(crypto: Crypto) : EncryptedProtoSerializer<VTMGOCredentials>(
    crypto,
    VTMGOCredentials(),
    VTMGOCredentials.ADAPTER
)
