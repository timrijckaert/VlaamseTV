package be.tapped.vlaamsetv.prefs.vtm

import be.tapped.vlaamsetv.auth.vtm.TokenWrapper
import be.tapped.vlaamsetv.auth.vtm.VTMGOCredentials
import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.EncryptedProtoSerializer

class TokenWrapperSerializer(crypto: Crypto) :
    EncryptedProtoSerializer<TokenWrapper>(crypto, TokenWrapper(), TokenWrapper.ADAPTER)

class VTMCredentialsSerializer(crypto: Crypto) :
    EncryptedProtoSerializer<VTMGOCredentials>(
        crypto,
        VTMGOCredentials(),
        VTMGOCredentials.ADAPTER
    )
