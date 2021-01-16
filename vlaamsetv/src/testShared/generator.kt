package be.tapped.vlaamsetv

import be.tapped.vier.profile.IdToken
import be.tapped.vlaamsetv.auth.prefs.Credential
import be.tapped.vrtnu.profile.AccessToken
import be.tapped.vrtnu.profile.Expiry
import be.tapped.vrtnu.profile.RefreshToken
import be.tapped.vrtnu.profile.TokenWrapper
import be.tapped.vrtnu.profile.XVRTToken
import be.tapped.vtmgo.profile.JWT
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take

val vrtTokenWrapperArb: Arb<TokenWrapper> = arbitrary {
    val accessToken = AccessToken(Arb.string(minSize = 1).gen())
    val refreshToken = RefreshToken(Arb.string(minSize = 1).gen())
    val expiry = Expiry(Arb.long().gen())
    TokenWrapper(
        accessToken,
        refreshToken,
        expiry,
    )
}

val errorMessageArb: Arb<ErrorMessage> = arbitrary {
    val error = Arb.int().gen()
    val extras = Arb.string().genList()

    ErrorMessage(error, extras)
}

val vierAccessTokenArb: Arb<be.tapped.vier.profile.AccessToken> =
    arbitrary { be.tapped.vier.profile.AccessToken(Arb.string().gen()) }
val vierRefreshTokenArb: Arb<be.tapped.vier.profile.RefreshToken> =
    arbitrary { be.tapped.vier.profile.RefreshToken(Arb.string().gen()) }
val vierIdTokenARb: Arb<IdToken> = arbitrary { IdToken(Arb.string().gen()) }
val vierTokenArb: Arb<be.tapped.vier.profile.TokenWrapper> = arbitrary {
    val accessToken = vierAccessTokenArb.gen()
    val expiresIn = vierExpiryArb.gen()
    val tokenType = Arb.string().gen()
    val refreshToken = vierRefreshTokenArb.gen()
    val idToken = vierIdTokenARb.gen()
    be.tapped.vier.profile.TokenWrapper(
        accessToken,
        expiresIn,
        tokenType,
        refreshToken,
        idToken,
    )
}

val vierExpiryArb: Arb<be.tapped.vier.profile.Expiry> = arbitrary { be.tapped.vier.profile.Expiry(Arb.long().gen()) }
val vtmJWTArb: Arb<JWT> = arbitrary { JWT(Arb.string().gen()) }
val expiryArb: Arb<be.tapped.vtmgo.profile.Expiry> = arbitrary { be.tapped.vtmgo.profile.Expiry(Arb.long().gen()) }
val vtmTokenWrapperArb: Arb<be.tapped.vtmgo.profile.TokenWrapper> = arbitrary {
    be.tapped.vtmgo.profile.TokenWrapper(vtmJWTArb.gen(), expiryArb.gen())
}
val xVRTTokenArb: Arb<XVRTToken> = arbitrary { XVRTToken(Arb.string().gen()) }

val vrtRefreshTokenArb: Arb<RefreshToken> = arbitrary { RefreshToken(Arb.string().gen()) }

val credentialsArb: Arb<Credential> = arbitrary { Credential(Arb.string().gen(), Arb.string().gen()) }

fun <T> Arb<T>.genList(amount: Int = 5, rs: RandomSource = RandomSource.Default): List<T> = take(amount, rs).toList()

fun <T> Arb<T>.gen(rs: RandomSource = RandomSource.Default): T = genList(1, rs).first()
