package be.tapped.vlaamsetv.auth

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import arrow.fx.coroutines.parMapN
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.auth.prefs.vrt.VRTTokenStore
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.profile.TokenRepo

class VRTTokenUseCase(
    private val tokenRepo: TokenRepo,
    private val dataStore: VRTTokenStore,
    private val vrtErrorMessageConverter: ErrorMessageConverter<ApiResponse.Failure>,
    private val tokenRefreshWorkScheduler: TokenRefreshWorkScheduler,
) : TokenUseCase {

    override suspend fun performLogin(
        username: String,
        password: String
    ): Either<ErrorMessage, Unit> =
        either {
            !checkPreconditions(username, password)
            val tokenWrapperWithXVRTToken =
                either<ApiResponse.Failure, Pair<ApiResponse.Success.Authentication.Token, ApiResponse.Success.Authentication.VRTToken>> {
                    parMapN(
                        { !tokenRepo.fetchTokenWrapper(username, password) },
                        { !tokenRepo.fetchXVRTToken(username, password) },
                        ::Pair
                    )
                }
            !when (tokenWrapperWithXVRTToken) {
                is Either.Left -> vrtErrorMessageConverter.mapToHumanReadableError(
                    tokenWrapperWithXVRTToken.a
                ).left()
                is Either.Right -> {
                    with(dataStore) {
                        saveVRTCredentials(username, password)
                        saveTokenWrapper(tokenWrapperWithXVRTToken.b.first.tokenWrapper)
                        saveXVRTToken(tokenWrapperWithXVRTToken.b.second.xVRTToken)
                    }
                    tokenRefreshWorkScheduler.scheduleTokenRefreshVRT()
                    Unit.right()
                }
            }
        }

    override suspend fun refresh(): Either<ErrorMessage, Boolean> {
        val refreshToken = dataStore.token()?.refreshToken ?: return false.right()
        return when (val tokenWrapper = tokenRepo.refreshTokenWrapper(refreshToken)) {
            is Either.Left -> vrtErrorMessageConverter.mapToHumanReadableError(tokenWrapper.a)
                .left()
            is Either.Right -> {
                dataStore.saveTokenWrapper(tokenWrapper.b.tokenWrapper)
                true.right()
            }
        }
    }
}
