package be.tapped.vlaamsetv.auth

import arrow.core.Either

class VTMAuthenticationUIController(
    private val vtmTokenUseCase: VTMTokenUseCase,
    private val authenticationNavigator: AuthenticationNavigator,
    private val authenticationState: AuthenticationState,
) : AuthenticationUIController {
    override suspend fun login(username: String, password: String) {
        when (val jwt = vtmTokenUseCase.performLogin(username, password)) {
            is Either.Left -> authenticationNavigator.navigateToErrorScreen(jwt.a)
            is Either.Right -> {
                authenticationState.updateAuthenticationState(
                    AuthenticationState.Brand.VTM,
                    AuthenticationState.Type.LOGGED_IN
                )
                authenticationNavigator.navigateNext()
            }
        }
    }

    override suspend fun next() {
        authenticationState.updateAuthenticationState(
            AuthenticationState.Brand.VTM,
            AuthenticationState.Type.SKIPPED
        )
        authenticationNavigator.navigateNext()
    }
}
