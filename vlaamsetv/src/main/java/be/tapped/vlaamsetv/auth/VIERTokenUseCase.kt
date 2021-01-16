package be.tapped.vlaamsetv.auth

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import be.tapped.vier.ApiResponse
import be.tapped.vier.profile.ProfileRepo
import be.tapped.vlaamsetv.auth.prefs.vier.VIERTokenStore

class VIERTokenUseCase(
    private val profileRepo: ProfileRepo,
    private val vierTokenStore: VIERTokenStore,
) : TokenUseCase<ApiResponse.Failure> {
    override suspend fun performLogin(
        username: String,
        password: String
    ): Either<ApiResponse.Failure, Unit> =
        when (val token = profileRepo.fetchTokens(username, password)) {
            is Either.Left -> token.a.left()
            is Either.Right -> {
                vierTokenStore.saveVierCredentials(username, password)
                vierTokenStore.saveToken(token.b)
                Unit.right()
            }
        }

    override suspend fun refresh(): Either<ApiResponse.Failure, Boolean> {
        val refreshToken = vierTokenStore.token()?.refreshToken ?: return false.right()
        return when (val newTokens = profileRepo.refreshTokens(refreshToken)) {
            is Either.Left -> newTokens.a.left()
            is Either.Right -> {
                vierTokenStore.saveToken(newTokens.b)
                true.right()
            }
        }
    }
}
