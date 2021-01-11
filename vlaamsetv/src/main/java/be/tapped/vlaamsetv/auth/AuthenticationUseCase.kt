package be.tapped.vlaamsetv.auth

import arrow.core.Either
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.ErrorMessageConverter
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.prefs.VRTTokenStore
import be.tapped.vrtnu.profile.TokenRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface AuthenticationUseCase {

    suspend fun login(username: String, password: String)

    suspend fun skip()

    val state: Flow<State>

    sealed class State {
        data class Fail(internal val errorMessage: ErrorMessage) : State()
        object Successful : State()
    }
}

class VRTAuthenticationUseCase(
    private val tokenRepo: TokenRepo,
    private val dataStore: VRTTokenStore,
    private val authenticationNavigator: AuthenticationNavigator,
    private val errorMessageConverter: ErrorMessageConverter
) : AuthenticationUseCase {

    override suspend fun login(username: String, password: String) {
        if (username.isBlank()) {
            _state.emit(AuthenticationUseCase.State.Fail(ErrorMessage(R.string.failure_generic_no_email)))
            return
        }

        if (password.isBlank()) {
            _state.emit(AuthenticationUseCase.State.Fail(ErrorMessage(R.string.failure_generic_no_password)))
            return
        }

        val tokenWrapper =
            tokenRepo.fetchTokenWrapper(username, password)
        _state.emit(
            when (tokenWrapper) {
                is Either.Left -> {
                    val errorMessage = errorMessageConverter.mapToHumanReadableError(tokenWrapper.a)
                    AuthenticationUseCase.State.Fail(errorMessage)
                }
                is Either.Right -> {
                    dataStore.saveTokenWrapper(tokenWrapper.b.tokenWrapper)
                    authenticationNavigator.navigateNext()
                    AuthenticationUseCase.State.Successful
                }
            }
        )
    }

    override suspend fun skip() {
        authenticationNavigator.navigateNext()
    }

    private val _state: MutableSharedFlow<AuthenticationUseCase.State> = MutableSharedFlow(1)

    override val state: Flow<AuthenticationUseCase.State> get() = _state.asSharedFlow()
}

class VTMAuthenticationUseCase : AuthenticationUseCase {
    override suspend fun login(username: String, password: String) {

    }

    override suspend fun skip() {

    }

    private val _state: MutableSharedFlow<AuthenticationUseCase.State> = MutableSharedFlow(1)

    override val state: Flow<AuthenticationUseCase.State> get() = _state.asSharedFlow()
}
