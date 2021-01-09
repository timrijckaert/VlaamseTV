package be.tapped.vlaamsetv.auth

import arrow.core.Either
import be.tapped.vlaamsetv.prefs.VRTTokenStore
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.profile.LoginFailure
import be.tapped.vrtnu.profile.TokenRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal interface AuthenticationUseCase {

    suspend fun login(username: String, password: String)

    suspend fun skip()

    val state: StateFlow<State>

    sealed class State {
        object Empty : State()
        data class Fail(internal val message: String) : State()
        object Successful : State()
    }
}

internal class VRTAuthenticationUseCase(
    private val tokenRepo: TokenRepo,
    private val vrtTokenStore: VRTTokenStore
) : AuthenticationUseCase {

    override suspend fun login(username: String, password: String) {
        val tokenWrapper =
            tokenRepo.fetchTokenWrapper(username, password)
        _state.value = when (tokenWrapper) {
            is Either.Left ->
                AuthenticationUseCase.State.Fail(mapAuthenticationFailureToUserMessage(tokenWrapper))
            is Either.Right -> {
                vrtTokenStore.saveTokenWrapper(tokenWrapper.b.tokenWrapper)
                AuthenticationUseCase.State.Successful
            }
        }
    }

    //TODO
    // maybe this needs to be easier in that sense that a user does not really care it just went wrong for him.
    // Write these errors to a file for reporting reasons
    private fun mapAuthenticationFailureToUserMessage(tokenWrapper: Either.Left<ApiResponse.Failure>) =
        when (val failure = tokenWrapper.a) {
            is ApiResponse.Failure.NetworkFailure -> "Network error: ${failure.responseCode}"
            is ApiResponse.Failure.JsonParsingException -> "JSON parsing exception: ${failure.throwable.message}"
            ApiResponse.Failure.EmptyJson -> "No JSON response"
            is ApiResponse.Failure.Authentication.FailedToLogin -> when (failure.loginResponseFailure.loginFailure) {
                LoginFailure.LoginFailure.INVALID_CREDENTIALS -> "Geen geldige logingegevens of wachtwoord"
                LoginFailure.LoginFailure.MISSING_LOGIN_ID -> "Je hebt geen email adres ingevoerd"
                LoginFailure.LoginFailure.MISSING_PASSWORD -> "Je hebt geen wachtwoord ingevoerd"
                LoginFailure.LoginFailure.UNKNOWN -> "Geen idee wat er mis ging bij het aanmelden."
            }
            is ApiResponse.Failure.Authentication.MissingCookieValues -> "Missing cookies ${failure.cookieValues}"
            is ApiResponse.Failure.Content.SearchQuery -> TODO()
            else -> "Unknown error"
        }

    override suspend fun skip() {
    }

    private val _state: MutableStateFlow<AuthenticationUseCase.State> =
        MutableStateFlow(AuthenticationUseCase.State.Empty)

    override val state: StateFlow<AuthenticationUseCase.State> get() = _state.asStateFlow()
}
