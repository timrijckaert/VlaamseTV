package be.tapped.vlaamsetv.auth

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import arrow.fx.coroutines.parZip
import be.tapped.goplay.profile.ProfileRepo
import be.tapped.vlaamsetv.auth.prefs.goplay.GoPlayTokenStore
import be.tapped.vlaamsetv.auth.prefs.vrt.VRTTokenStore
import be.tapped.vlaamsetv.auth.prefs.vtm.VTMTokenStore
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.profile.TokenRepo
import be.tapped.vtmgo.profile.AuthenticationRepo

interface TokenUseCase {

    suspend fun performLogin(username: String, password: String): Either<Unit, Unit>

    suspend fun refresh(): Either<Unit, Boolean>

}

class VRTTokenUseCase(
    private val tokenRepo: TokenRepo,
    private val dataStore: VRTTokenStore,
    private val tokenRefreshWorkScheduler: TokenRefreshWorkScheduler,
) : TokenUseCase {

    override suspend fun performLogin(username: String, password: String): Either<Unit, Unit> = either {
        val tokenWrapperWithXVRTToken: Either<ApiResponse.Failure, Pair<ApiResponse.Success.Authentication.Token, ApiResponse.Success.Authentication.VRTToken>> =
            parZip(
                { tokenRepo.fetchTokenWrapper(username, password) },
                { tokenRepo.fetchXVRTToken(username, password) }) { a, b ->
                either { Pair(a.bind(), b.bind()) }
            }

        when (tokenWrapperWithXVRTToken) {
            is Either.Left -> Unit.left()
            is Either.Right -> {
                with(dataStore) {
                    saveVRTCredentials(username, password)
                    saveTokenWrapper(tokenWrapperWithXVRTToken.value.first.tokenWrapper)
                    saveXVRTToken(tokenWrapperWithXVRTToken.value.second.xVRTToken)
                }
                tokenRefreshWorkScheduler.scheduleTokenRefreshVRT()
                Unit.right()
            }
        }.bind()
    }

    override suspend fun refresh(): Either<Unit, Boolean> {
        val refreshToken = dataStore.token()?.refreshToken ?: return false.right()
        return when (val tokenWrapper = tokenRepo.refreshTokenWrapper(refreshToken)) {
            is Either.Left -> Unit.left()
            is Either.Right -> {
                dataStore.saveTokenWrapper(tokenWrapper.value.tokenWrapper)
                true.right()
            }
        }
    }
}

class VTMTokenUseCase(
    private val authenticationRepo: AuthenticationRepo,
    private val vtmTokenStore: VTMTokenStore,
    private val tokenRefreshWorkerScheduler: TokenRefreshWorkScheduler,
) : TokenUseCase {

    override suspend fun performLogin(username: String, password: String): Either<Unit, Unit> =
        either {
            when (val jwt = authenticationRepo.login(username, password)) {
                is Either.Left -> Unit.left()
                is Either.Right -> {
                    vtmTokenStore.saveVTMCredentials(username, password)
                    vtmTokenStore.saveToken(jwt.value.token)
                    tokenRefreshWorkerScheduler.scheduleTokenRefreshVTM()
                    Unit.right()
                }
            }.bind()
        }

    override suspend fun refresh(): Either<Unit, Boolean> {
        val (username, password) = vtmTokenStore.vtmCredentials() ?: return false.right()
        //TODO VTM GO refresh token possibility?
        return performLogin(username, password).map { true }
    }
}

class GoPlayTokenUseCase(
    private val profileRepo: ProfileRepo,
    private val goPlayTokenStore: GoPlayTokenStore,
    private val tokenRefreshWorkScheduler: TokenRefreshWorkScheduler,
) : TokenUseCase {

    override suspend fun performLogin(username: String, password: String): Either<Unit, Unit> =
        when (val token = profileRepo.fetchTokens(username, password)) {
            is Either.Left -> Unit.left()
            is Either.Right -> {
                with(goPlayTokenStore) {
                    saveGoPlayCredentials(username, password)
                    saveToken(token.value.token)
                }
                tokenRefreshWorkScheduler.scheduleTokenRefreshGoPlay()
                Unit.right()
            }
        }

    override suspend fun refresh(): Either<Unit, Boolean> {
        val refreshToken = goPlayTokenStore.token()?.refreshToken ?: return false.right()
        return when (val newTokens = profileRepo.refreshTokens(refreshToken)) {
            is Either.Left -> Unit.left()
            is Either.Right -> {
                goPlayTokenStore.saveToken(newTokens.value.token)
                true.right()
            }
        }
    }
}
