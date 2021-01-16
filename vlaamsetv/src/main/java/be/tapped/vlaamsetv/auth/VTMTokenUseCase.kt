package be.tapped.vlaamsetv.auth

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.auth.prefs.vtm.VTMTokenStore
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.profile.HttpProfileRepo

class VTMTokenUseCase(
    private val profileRepo: HttpProfileRepo,
    private val vtmTokenStore: VTMTokenStore,
) : TokenUseCase<ApiResponse.Failure> {
    override suspend fun performLogin(
        username: String,
        password: String
    ): Either<ApiResponse.Failure, Unit> =
        when (val jwt = profileRepo.login(username, password)) {
            is Either.Left -> jwt.a.left()
            is Either.Right -> {
                vtmTokenStore.saveVTMCredentials(username, password)
                vtmTokenStore.saveToken(jwt.b.token)
                Unit.right()
            }
        }

    override suspend fun refresh(): Either<ApiResponse.Failure, Boolean> {
        val (username, password) = vtmTokenStore.vtmCredentials() ?: return false.right()
        //TODO VTM GO refresh token possibility?
        return performLogin(username, password).map { true }
    }
}
