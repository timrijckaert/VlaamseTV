package be.tapped.vlaamsetv.auth.prefs.goplay

import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.EncryptedProtoSerializer

class TokenSerializer(crypto: Crypto) : EncryptedProtoSerializer<Token>(crypto, Token(), Token.ADAPTER)

class GoPlayCredentialsSerializer(crypto: Crypto) : EncryptedProtoSerializer<GoPlayCredentials>(
    crypto,
    GoPlayCredentials(),
    GoPlayCredentials.ADAPTER
)
