package be.tapped.vlaamsetv.auth

import arrow.core.Either
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.VRTErrorMessageConverter
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStore
import be.tapped.vrtnu.profile.TokenRepo

class VRTAuthenticationUseCase(
    private val tokenRepo: TokenRepo,
    private val dataStore: VRTTokenStore,
    private val authenticationNavigator: AuthenticationNavigator,
    private val VRTErrorMessageConverter: VRTErrorMessageConverter
) : AuthenticationUseCase {

    override suspend fun login(username: String, password: String) {
        if (checkPreconditions(username, password)) return

        // Simon 🪄🧙🏻
        // Combine fetchXVRTToken + fetchTokenWrapper
        // Run async
        // Wait for both
        //// Only if both are true we continue
        //// Emit error on first occurrence
        // 1. tokenRepo.fetchXVRTToken(username, password) //Either<ApiResponse.Failure, ApiResponse.Success.Authentication.VRTToken>
        // 2. tokenRepo.fetchTokenWrapper(username, password) //Either<ApiResponse.Failure, ApiResponse.Success.Authentication.Token>

        // Don't worry about code underneath
        when (val tokenWrapper = tokenRepo.fetchTokenWrapper(username, password)) {
            is Either.Left -> {
                authenticationNavigator.navigateToErrorScreen(
                    VRTErrorMessageConverter.mapToHumanReadableError(
                        tokenWrapper.a
                    )
                )
            }
            is Either.Right -> {
                dataStore.saveTokenWrapper(tokenWrapper.b.tokenWrapper)
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
