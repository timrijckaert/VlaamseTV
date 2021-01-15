package be.tapped.vlaamsetv.auth

import arrow.core.Either
import be.tapped.vier.ApiResponse
import be.tapped.vier.profile.HttpProfileRepo
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.auth.prefs.vier.VIERTokenStore

class VIERAuthenticationUIController(
    private val profileRepo: HttpProfileRepo,
    private val vierTokenStore: VIERTokenStore,
    private val authenticationNavigator: AuthenticationNavigator,
    private val errorMessageConverter: ErrorMessageConverter<ApiResponse.Failure>,
) : AuthenticationUIController {

    override suspend fun login(username: String, password: String) {
        if (checkPreconditions(username, password)) return
        when (val token = profileRepo.fetchTokens(username, password)) {
            is Either.Left -> {
                authenticationNavigator.navigateToErrorScreen(
                    errorMessageConverter.mapToHumanReadableError(token.a)
                )
            }
            is Either.Right -> {
                vierTokenStore.saveVierCredentials(username, password)
                vierTokenStore.saveToken(token.b)
                authenticationNavigator.navigateNext()
            }
        }
    }

    override suspend fun skip() {
        authenticationNavigator.navigateNext()
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
}
