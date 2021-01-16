package be.tapped.vlaamsetv

import be.tapped.vier.ApiResponse
import be.tapped.vier.profile.IdToken
import be.tapped.vlaamsetv.auth.prefs.Credential
import be.tapped.vrtnu.profile.*
import be.tapped.vtmgo.profile.JWT
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.*

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

    ErrorMessage(
        error,
        extras
    )
}

val vierAccessTokenArb: Arb<be.tapped.vier.profile.AccessToken> =
    arbitrary { be.tapped.vier.profile.AccessToken(Arb.string().gen()) }
val vierRefreshTokenArb: Arb<be.tapped.vier.profile.RefreshToken> =
    arbitrary { be.tapped.vier.profile.RefreshToken(Arb.string().gen()) }
val vierIdTokenARb: Arb<IdToken> = arbitrary { IdToken(Arb.string().gen()) }
val vierTokenArb: Arb<ApiResponse.Success.Authentication.Token> = arbitrary {
    val accessToken = vierAccessTokenArb.gen()
    val expiresIn = vierExpiryArb.gen()
    val tokenType = Arb.string().gen()
    val refreshToken = vierRefreshTokenArb.gen()
    val idToken = vierIdTokenARb.gen()
    ApiResponse.Success.Authentication.Token(
        accessToken,
        expiresIn,
        tokenType,
        refreshToken,
        idToken,
    )
}

val vierExpiryArb: Arb<be.tapped.vier.profile.Expiry> =
    arbitrary { be.tapped.vier.profile.Expiry(Arb.long().gen()) }
val vtmJWTArb: Arb<JWT> = arbitrary { JWT(Arb.string().gen()) }
val expiryArb: Arb<be.tapped.vtmgo.profile.Expiry> =
    arbitrary { be.tapped.vtmgo.profile.Expiry(Arb.long().gen()) }
val vtmTokenWrapperArb: Arb<be.tapped.vtmgo.profile.TokenWrapper> = arbitrary {
    be.tapped.vtmgo.profile.TokenWrapper(
        vtmJWTArb.gen(),
        expiryArb.gen()
    )
}
val xVRTTokenArb: Arb<XVRTToken> = arbitrary { XVRTToken(Arb.string().gen()) }

val vrtRefreshTokenArb: Arb<RefreshToken> = arbitrary { RefreshToken(Arb.string().gen()) }

val credentialsArb: Arb<Credential> =
    arbitrary { Credential(Arb.string().gen(), Arb.string().gen()) }

fun <T> Arb<T>.genList(amount: Int = 5, rs: RandomSource = RandomSource.Default): List<T> =
    take(amount, rs).toList()

fun <T> Arb<T>.gen(rs: RandomSource = RandomSource.Default): T = genList(1, rs).first()
