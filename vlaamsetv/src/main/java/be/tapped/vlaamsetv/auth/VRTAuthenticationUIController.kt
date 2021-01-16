package be.tapped.vlaamsetv.auth

import arrow.core.Either

class VRTAuthenticationUIController(
    private val vrtTokenUseCase: VRTTokenUseCase,
    private val authenticationNavigator: AuthenticationNavigator
) : AuthenticationUIController {

    override suspend fun login(username: String, password: String) {
        when (val loginResult = vrtTokenUseCase.performLogin(username, password)) {
            is Either.Left -> authenticationNavigator.navigateToErrorScreen(loginResult.a)
            is Either.Right -> authenticationNavigator.navigateNext()
        }
    }

    override suspend fun next() {
        authenticationNavigator.navigateNext()
    }
}
