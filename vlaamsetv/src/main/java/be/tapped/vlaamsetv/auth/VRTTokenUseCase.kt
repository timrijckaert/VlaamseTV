package be.tapped.vlaamsetv.auth

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import arrow.fx.coroutines.parMapN
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStore
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.profile.TokenRepo

class VRTTokenUseCase(
    private val tokenRepo: TokenRepo,
    private val dataStore: VRTTokenStore,
) : TokenUseCase<ApiResponse.Failure> {

    override suspend fun performLogin(
        username: String,
        password: String
    ): Either<ApiResponse.Failure, Unit> {
        val tokenWrapperWithXVRTToken =
            either<ApiResponse.Failure, Pair<ApiResponse.Success.Authentication.Token, ApiResponse.Success.Authentication.VRTToken>> {
                parMapN(
                    { !tokenRepo.fetchTokenWrapper(username, password) },
                    { !tokenRepo.fetchXVRTToken(username, password) },
                    ::Pair
                )
            }
        return when (tokenWrapperWithXVRTToken) {
            is Either.Left -> tokenWrapperWithXVRTToken.a.left()
            is Either.Right -> {
                with(dataStore) {
                    saveVRTCredentials(username, password)
                    saveTokenWrapper(tokenWrapperWithXVRTToken.b.first.tokenWrapper)
                    saveXVRTToken(tokenWrapperWithXVRTToken.b.second.xVRTToken)
                }
                Unit.right()
            }
        }
    }

    override suspend fun refresh(): Either<ApiResponse.Failure, Boolean> {
        val refreshToken =
            dataStore.token()?.refreshToken ?: return false.right()
        return when (val tokenWrapper = tokenRepo.refreshTokenWrapper(refreshToken)) {
            is Either.Left -> tokenWrapper.a.left()
            is Either.Right -> {
                dataStore.saveTokenWrapper(tokenWrapper.b.tokenWrapper)
                true.right()
            }
        }
    }
}
