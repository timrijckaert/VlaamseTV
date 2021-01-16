package be.tapped.vlaamsetv.auth

import arrow.core.Either

class VTMAuthenticationUIController(
    private val vtmTokenUseCase: VTMTokenUseCase,
    private val authenticationNavigator: AuthenticationNavigator,
) : AuthenticationUIController {
    override suspend fun login(username: String, password: String) {
        when (val jwt = vtmTokenUseCase.performLogin(username, password)) {
            is Either.Left -> authenticationNavigator.navigateToErrorScreen(jwt.a)
            is Either.Right -> authenticationNavigator.navigateNext()
        }
    }

    override suspend fun next() {
        authenticationNavigator.navigateNext()
    }
}
