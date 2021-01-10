package be.tapped.vlaamsetv

import be.tapped.vrtnu.profile.AccessToken
import be.tapped.vrtnu.profile.Expiry
import be.tapped.vrtnu.profile.RefreshToken
import be.tapped.vrtnu.profile.TokenWrapper
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take

val tokenWrapperArb: Arb<TokenWrapper> = arbitrary {
    val accessToken = AccessToken(Arb.string(minSize = 1).gen())
    val refreshToken = RefreshToken(Arb.string(minSize = 1).gen())
    val expiry = Expiry(Arb.long().gen())
    TokenWrapper(
        accessToken,
        refreshToken,
        expiry,
    )
}

fun <T> Arb<T>.genList(amount: Int = 5, rs: RandomSource = RandomSource.Default): List<T> =
    take(amount, rs).toList()

fun <T> Arb<T>.gen(rs: RandomSource = RandomSource.Default): T = genList(1, rs).first()
