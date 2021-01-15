package be.tapped.vlaamsetv.auth

import arrow.core.Either
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.VRTErrorMessageConverter
import be.tapped.vrtnu.ApiResponse

class VRTAuthenticationUIController(
    private val vrtTokenUseCase: TokenUseCase<ApiResponse.Failure>,
    private val authenticationNavigator: AuthenticationNavigator,
    private val VRTErrorMessageConverter: VRTErrorMessageConverter
) : AuthenticationUIController {

    override suspend fun login(username: String, password: String) {
        if (checkPreconditions(username, password)) return

        when (val wasSuccessfullyLoggedIn = vrtTokenUseCase.performLogin(username, password)) {
            is Either.Left -> {
                val errorMessage =
                    VRTErrorMessageConverter.mapToHumanReadableError(wasSuccessfullyLoggedIn.a)
                authenticationNavigator.navigateToErrorScreen(errorMessage)
            }
            is Either.Right -> {
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
