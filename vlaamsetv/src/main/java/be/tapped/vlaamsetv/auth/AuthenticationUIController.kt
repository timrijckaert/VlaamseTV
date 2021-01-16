package be.tapped.vlaamsetv.auth

import arrow.core.Either

interface AuthenticationUIController {

    suspend fun login(username: String, password: String)

    suspend fun next()

}

class VTMAuthenticationUIController(
    private val vtmTokenUseCase: VTMTokenUseCase,
    private val authenticationNavigator: AuthenticationNavigator,
    private val authenticationState: AuthenticationState,
) : AuthenticationUIController {

    override suspend fun login(username: String, password: String) {
        when (val jwt = vtmTokenUseCase.performLogin(username, password)) {
            is Either.Left -> authenticationNavigator.navigateToErrorScreen(jwt.a)
            is Either.Right -> {
                authenticationState.updateAuthenticationState(AuthenticationState.Brand.VTM, AuthenticationState.Type.LOGGED_IN)
                authenticationNavigator.navigateNext()
            }
        }
    }

    override suspend fun next() {
        authenticationState.updateAuthenticationState(AuthenticationState.Brand.VTM, AuthenticationState.Type.SKIPPED)
        authenticationNavigator.navigateNext()
    }
}

class VRTAuthenticationUIController(
    private val vrtTokenUseCase: VRTTokenUseCase,
    private val authenticationNavigator: AuthenticationNavigator,
    private val authenticationState: AuthenticationState,
) : AuthenticationUIController {

    override suspend fun login(username: String, password: String) {
        when (val loginResult = vrtTokenUseCase.performLogin(username, password)) {
            is Either.Left -> authenticationNavigator.navigateToErrorScreen(loginResult.a)
            is Either.Right -> {
                authenticationNavigator.navigateNext()
                authenticationState.updateAuthenticationState(AuthenticationState.Brand.VRT, AuthenticationState.Type.LOGGED_IN)
            }
        }
    }

    override suspend fun next() {
        authenticationState.updateAuthenticationState(AuthenticationState.Brand.VRT, AuthenticationState.Type.SKIPPED)
        authenticationNavigator.navigateNext()
    }
}

class VIERAuthenticationUIController(
    private val vierTokenUseCase: VIERTokenUseCase,
    private val authenticationNavigator: AuthenticationNavigator,
    private val authenticationState: AuthenticationState,
) : AuthenticationUIController {

    override suspend fun login(username: String, password: String) {
        when (val token = vierTokenUseCase.performLogin(username, password)) {
            is Either.Left -> authenticationNavigator.navigateToErrorScreen(token.a)
            is Either.Right -> {
                authenticationState.updateAuthenticationState(AuthenticationState.Brand.VIER, AuthenticationState.Type.LOGGED_IN)
                authenticationNavigator.navigateNext()
            }
        }
    }

    override suspend fun next() {
        authenticationState.updateAuthenticationState(AuthenticationState.Brand.VIER, AuthenticationState.Type.SKIPPED)
        authenticationNavigator.navigateNext()
    }
}
