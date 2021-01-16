package be.tapped.vlaamsetv.auth

import arrow.core.Either

class VRTAuthenticationUIController(
    private val vrtTokenUseCase: VRTTokenUseCase,
    private val authenticationNavigator: AuthenticationNavigator,
    private val authenticationState: AuthenticationState
) : AuthenticationUIController {

    override suspend fun login(username: String, password: String) {
        when (val loginResult = vrtTokenUseCase.performLogin(username, password)) {
            is Either.Left -> authenticationNavigator.navigateToErrorScreen(loginResult.a)
            is Either.Right -> {
                authenticationNavigator.navigateNext()
                authenticationState.updateAuthenticationState(
                    AuthenticationState.Brand.VRT,
                    AuthenticationState.Type.LOGGED_IN
                )
            }
        }
    }

    override suspend fun next() {
        authenticationState.updateAuthenticationState(
            AuthenticationState.Brand.VRT,
            AuthenticationState.Type.SKIPPED
        )
        authenticationNavigator.navigateNext()
    }
}
