package be.tapped.vlaamsetv.auth

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import be.tapped.vier.ApiResponse
import be.tapped.vier.profile.ProfileRepo
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.auth.prefs.vier.VIERTokenStore

class VIERTokenUseCase(
    private val profileRepo: ProfileRepo,
    private val vierTokenStore: VIERTokenStore,
    private val vierErrorMessageConverter: ErrorMessageConverter<ApiResponse.Failure>,
) : TokenUseCase {
    override suspend fun performLogin(
        username: String,
        password: String
    ): Either<ErrorMessage, Unit> =
        either {
            !checkPreconditions(username, password)
            when (val token = profileRepo.fetchTokens(username, password)) {
                is Either.Left -> vierErrorMessageConverter.mapToHumanReadableError(token.a).left()
                is Either.Right -> {
                    vierTokenStore.saveVierCredentials(username, password)
                    vierTokenStore.saveToken(token.b.token)
                    Unit.right()
                }
            }
        }

    override suspend fun refresh(): Either<ErrorMessage, Boolean> {
        val refreshToken = vierTokenStore.token()?.refreshToken ?: return false.right()
        return when (val newTokens = profileRepo.refreshTokens(refreshToken)) {
            is Either.Left -> vierErrorMessageConverter.mapToHumanReadableError(newTokens.a).left()
            is Either.Right -> {
                vierTokenStore.saveToken(newTokens.b.token)
                true.right()
            }
        }
    }
}
