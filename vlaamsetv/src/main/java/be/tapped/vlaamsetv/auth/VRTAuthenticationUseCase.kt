package be.tapped.vlaamsetv.auth

import arrow.core.Either
import arrow.core.computations.either
import arrow.fx.coroutines.parMapN
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.VRTErrorMessageConverter
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStore
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.profile.TokenRepo

class VRTAuthenticationUseCase(
        private val tokenRepo: TokenRepo,
        private val dataStore: VRTTokenStore,
        private val authenticationNavigator: AuthenticationNavigator,
        private val VRTErrorMessageConverter: VRTErrorMessageConverter
) : AuthenticationUseCase {

    override suspend fun login(username: String, password: String) {
        if (checkPreconditions(username, password)) return

        val tokenWrapperWithXVRTToken =
                either<ApiResponse.Failure, Pair<ApiResponse.Success.Authentication.Token, ApiResponse.Success.Authentication.VRTToken>> {
                    parMapN(
                            { !tokenRepo.fetchTokenWrapper(username, password) },
                            { !tokenRepo.fetchXVRTToken(username, password) },
                            ::Pair
                    )
                }

        when (tokenWrapperWithXVRTToken) {
            is Either.Left -> {
                authenticationNavigator.navigateToErrorScreen(
                        VRTErrorMessageConverter.mapToHumanReadableError(
                                tokenWrapperWithXVRTToken.a
                        )
                )
            }
            is Either.Right -> {
                with(dataStore) {
                    saveVRTCredentials(username, password)
                    saveTokenWrapper(tokenWrapperWithXVRTToken.b.first.tokenWrapper)
                    saveXVRTToken(tokenWrapperWithXVRTToken.b.second.xVRTToken)
                }
                authenticationNavigator.navigateNext()
            }
        }
    }

    private fun checkPreconditions(
            username: String,
            password: String
    ): Boolean {
        if (username.isBlank()) {
            authenticationNavigator.navigateToErrorScreen(ErrorMessage(R.string.failure_generic_no_email))
            return true
        }

        if (password.isBlank()) {
            authenticationNavigator.navigateToErrorScreen(ErrorMessage(R.string.failure_generic_no_password))
            return true
        }
        return false
    }

    override suspend fun skip() {
        authenticationNavigator.navigateNext()
    }
}
