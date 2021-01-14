package be.tapped.vlaamsetv

import be.tapped.vier.ApiResponse
import be.tapped.vier.profile.IdToken
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

val vierTokenArb: Arb<ApiResponse.Success.Authentication.Token> = arbitrary {
    val accessToken = be.tapped.vier.profile.AccessToken(Arb.string().gen())
    val expiresIn = Arb.int().gen()
    val tokenType = Arb.string().gen()
    val refreshToken = be.tapped.vier.profile.RefreshToken(Arb.string().gen())
    val idToken = IdToken(Arb.string().gen())
    ApiResponse.Success.Authentication.Token(
        accessToken,
        expiresIn,
        tokenType,
        refreshToken,
        idToken,
    )
}

val vtmJWTArb: Arb<JWT> = arbitrary { JWT(Arb.string().gen()) }
val expiryArb: Arb<be.tapped.vtmgo.profile.Expiry> =
    arbitrary { be.tapped.vtmgo.profile.Expiry(Arb.long().gen()) }
val vtmTokenWrapper: Arb<be.tapped.vtmgo.profile.TokenWrapper> = arbitrary {
    be.tapped.vtmgo.profile.TokenWrapper(
        vtmJWTArb.gen(),
        expiryArb.gen()
    )
}
val xVRTTokenArb: Arb<XVRTToken> = arbitrary { XVRTToken(Arb.string().gen()) }

fun <T> Arb<T>.genList(amount: Int = 5, rs: RandomSource = RandomSource.Default): List<T> =
    take(amount, rs).toList()

fun <T> Arb<T>.gen(rs: RandomSource = RandomSource.Default): T = genList(1, rs).first()
