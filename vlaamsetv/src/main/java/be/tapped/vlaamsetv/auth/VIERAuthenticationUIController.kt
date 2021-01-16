package be.tapped.vlaamsetv.auth

import arrow.core.Either

class VIERAuthenticationUIController(
    private val vierTokenUseCase: VIERTokenUseCase,
    private val authenticationNavigator: AuthenticationNavigator,
    private val authenticationState: AuthenticationState,
) : AuthenticationUIController {

    override suspend fun login(username: String, password: String) {
        when (val token = vierTokenUseCase.performLogin(username, password)) {
            is Either.Left -> authenticationNavigator.navigateToErrorScreen(token.a)
            is Either.Right -> {
                authenticationState.updateAuthenticationState(
                    AuthenticationState.Brand.VIER,
                    AuthenticationState.Type.LOGGED_IN
                )
                authenticationNavigator.navigateNext()
            }
        }
    }

    override suspend fun next() {
        authenticationState.updateAuthenticationState(
            AuthenticationState.Brand.VIER,
            AuthenticationState.Type.SKIPPED
        )
        authenticationNavigator.navigateNext()
    }

}
