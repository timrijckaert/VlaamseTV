package be.tapped.vlaamsetv.prefs.vier

import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.EncryptedProtoSerializer

class TokenSerializer(crypto: Crypto) :
    EncryptedProtoSerializer<Token>(
        crypto,
        Token(),
        Token.ADAPTER
    )

class VIERCredentialsSerializer(crypto: Crypto) :
    EncryptedProtoSerializer<VIERCredentials>(
        crypto,
        VIERCredentials(),
        VIERCredentials.ADAPTER
    )
