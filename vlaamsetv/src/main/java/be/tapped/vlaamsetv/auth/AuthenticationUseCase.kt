package be.tapped.vlaamsetv.auth

import arrow.core.Either
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.profile.TokenRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.contracts.ExperimentalContracts

internal interface AuthenticationUseCase {
    enum class Brand {
        VRT_NU,
        VTM_GO,
        VIER
    }

    @OptIn(ExperimentalContracts::class)
    data class Credentials(
        private val brand: Brand,
        val username: String = "",
        val password: String = "",
    ) {
        val allFieldsAreFilledIn get() = hasUsername && hasPassword
        private val hasUsername: Boolean get() = username.isNotBlank()
        private val hasPassword get() = password.isNotBlank()
    }

    suspend fun login()

    suspend fun skip()

    var credentials: Credentials

    val state: StateFlow<State>

    sealed class State {
        object Empty : State()
        data class Fail(internal val message: String) : State()
        object Successful : State()
    }

    companion object {
        fun vrtAuthenticationUseCase(tokenRepo: TokenRepo): AuthenticationUseCase =
            object : AuthenticationUseCase {
                override var credentials: Credentials = Credentials(Brand.VRT_NU)

                override suspend fun login() {
                    val tokenWrapper =
                        tokenRepo.fetchTokenWrapper(credentials.username, credentials.password)
                    _state.value = when (tokenWrapper) {
                        is Either.Left ->
                            State.Fail(mapAuthenticationFailureToUserMessage(tokenWrapper))
                        is Either.Right -> State.Successful
                    }
                }

                private fun mapAuthenticationFailureToUserMessage(tokenWrapper: Either.Left<ApiResponse.Failure>) =
                    when (val failure = tokenWrapper.a) {
                        is ApiResponse.Failure.NetworkFailure -> "Netwerk error: ${failure.responseCode}"
                        is ApiResponse.Failure.JsonParsingException -> "JSON parsing exception: ${failure.throwable.message}"
                        ApiResponse.Failure.EmptyJson -> "No JSON response"
                        is ApiResponse.Failure.Authentication.FailedToLogin -> failure.loginResponseFailure.loginFailure.toString()
                        is ApiResponse.Failure.Authentication.MissingCookieValues -> "Missing cookies ${failure.cookieValues}"
                        else -> "Unknown error"
                    }

                override suspend fun skip() {
                }

                private val _state: MutableStateFlow<State> = MutableStateFlow(State.Empty)
                override val state: StateFlow<State> get() = _state
            }
    }
}
