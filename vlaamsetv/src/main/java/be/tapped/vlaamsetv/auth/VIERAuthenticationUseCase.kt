package be.tapped.vlaamsetv.auth

import arrow.core.Either
import be.tapped.vier.ApiResponse
import be.tapped.vier.profile.HttpProfileRepo
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.prefs.vier.VIERTokenStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class VIERAuthenticationUseCase(
    private val profileRepo: HttpProfileRepo,
    private val vierTokenStore: VIERTokenStore,
    private val authenticationNavigator: AuthenticationNavigator,
    private val errorMessageConverter: ErrorMessageConverter<ApiResponse.Failure>,
) : AuthenticationUseCase {

    override suspend fun login(username: String, password: String) {
        if (checkPreconditions(username, password)) return
        when (val token = profileRepo.fetchTokens(username, password)) {
            is Either.Left -> {
                _state.emit(
                    AuthenticationUseCase.State.Fail(
                        errorMessageConverter.mapToHumanReadableError(token.a)
                    )
                )
            }
            is Either.Right -> {
                vierTokenStore.saveVierCredentials(username, password)
                vierTokenStore.saveTokenWrapper(token.b)
                authenticationNavigator.navigateNext()
                _state.emit(AuthenticationUseCase.State.Successful)
            }
        }
    }

    override suspend fun skip() {
        authenticationNavigator.navigateNext()
    }

    private suspend fun checkPreconditions(
        username: String,
        password: String
    ): Boolean {
        if (username.isBlank()) {
            _state.emit(AuthenticationUseCase.State.Fail(ErrorMessage(R.string.failure_generic_no_email)))
            return true
        }

        if (password.isBlank()) {
            _state.emit(AuthenticationUseCase.State.Fail(ErrorMessage(R.string.failure_generic_no_password)))
            return true
        }
        return false
    }

    private val _state: MutableSharedFlow<AuthenticationUseCase.State> = MutableSharedFlow(1)

    override val state: Flow<AuthenticationUseCase.State> get() = _state.asSharedFlow()
}
