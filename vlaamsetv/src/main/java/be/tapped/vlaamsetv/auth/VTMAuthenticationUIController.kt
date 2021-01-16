package be.tapped.vlaamsetv.auth

import arrow.core.Either
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.R
import be.tapped.vtmgo.ApiResponse

class VTMAuthenticationUIController(
    private val vtmTokenUseCase: TokenUseCase<ApiResponse.Failure>,
    private val authenticationNavigator: AuthenticationNavigator,
    private val errorMessageConverter: ErrorMessageConverter<ApiResponse.Failure>,
) : AuthenticationUIController {
    override suspend fun login(username: String, password: String) {
        if (checkPreconditions(username, password)) return
        when (val jwt = vtmTokenUseCase.performLogin(username, password)) {
            is Either.Left ->
                authenticationNavigator.navigateToErrorScreen(
                    errorMessageConverter.mapToHumanReadableError(jwt.a)
                )
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
