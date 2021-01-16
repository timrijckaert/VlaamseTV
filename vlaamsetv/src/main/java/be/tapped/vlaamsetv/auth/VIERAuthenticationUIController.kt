package be.tapped.vlaamsetv.auth

import arrow.core.Either

class VIERAuthenticationUIController(
    private val vierTokenUseCase: VIERTokenUseCase,
    private val authenticationNavigator: AuthenticationNavigator,
) : AuthenticationUIController {

    override suspend fun login(username: String, password: String) {
        when (val token = vierTokenUseCase.performLogin(username, password)) {
            is Either.Left -> authenticationNavigator.navigateToErrorScreen(token.a)
            is Either.Right -> authenticationNavigator.navigateNext()
        }
    }

    override suspend fun next() {
        authenticationNavigator.navigateNext()
    }

    override suspend fun onUIShown() {
        if (vierTokenUseCase.hasCredentials()) {
            authenticationNavigator.navigateNext()
        }
    }
}
