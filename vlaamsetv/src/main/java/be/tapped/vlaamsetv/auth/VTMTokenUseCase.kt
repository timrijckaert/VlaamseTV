package be.tapped.vlaamsetv.auth

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.auth.prefs.vtm.VTMTokenStore
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.profile.AuthenticationRepo

class VTMTokenUseCase(
    private val authenticationRepo: AuthenticationRepo,
    private val vtmTokenStore: VTMTokenStore,
    private val vtmErrorMessageConverter: ErrorMessageConverter<ApiResponse.Failure>,
    private val tokenRefreshWorkerScheduler: TokenRefreshWorkScheduler,
) : TokenUseCase {
    override suspend fun performLogin(
        username: String,
        password: String
    ): Either<ErrorMessage, Unit> =
        either {
            !checkPreconditions(username, password)
            !when (val jwt = authenticationRepo.login(username, password)) {
                is Either.Left -> vtmErrorMessageConverter.mapToHumanReadableError(jwt.a).left()
                is Either.Right -> {
                    vtmTokenStore.saveVTMCredentials(username, password)
                    vtmTokenStore.saveToken(jwt.b.token)
                    tokenRefreshWorkerScheduler.scheduleTokenRefreshVTM()
                    Unit.right()
                }
            }
        }

    override suspend fun refresh(): Either<ErrorMessage, Boolean> {
        val (username, password) = vtmTokenStore.vtmCredentials() ?: return false.right()
        //TODO VTM GO refresh token possibility?
        return performLogin(username, password).map { true }
    }
}
