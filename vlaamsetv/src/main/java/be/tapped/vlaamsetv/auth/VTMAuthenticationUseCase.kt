package be.tapped.vlaamsetv.auth

import arrow.core.Either
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.prefs.vtm.VTMTokenStore
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.profile.HttpProfileRepo

class VTMAuthenticationUseCase(
    private val profileRepo: HttpProfileRepo,
    private val vtmTokenStore: VTMTokenStore,
    private val authenticationNavigator: AuthenticationNavigator,
    private val errorMessageConverter: ErrorMessageConverter<ApiResponse.Failure>,
) : AuthenticationUseCase {
    override suspend fun login(username: String, password: String) {
        if (checkPreconditions(username, password)) return
        when (val jwt = profileRepo.login(username, password)) {
            is Either.Left ->
                authenticationNavigator.navigateToErrorScreen(
                    errorMessageConverter.mapToHumanReadableError(jwt.a)
                )
            is Either.Right -> {
                vtmTokenStore.saveVTMCredentials(username, password)
                vtmTokenStore.saveToken(jwt.b.token)
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
