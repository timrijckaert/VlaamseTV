package be.tapped.vlaamsetv.prefs.vtm

import be.tapped.vlaamsetv.auth.vtm.JWT
import be.tapped.vlaamsetv.prefs.Crypto
import be.tapped.vlaamsetv.prefs.EncryptedProtoSerializer

class JWTProtoSerializer(crypto: Crypto) : EncryptedProtoSerializer<JWT>(crypto, JWT(), JWT.ADAPTER)
