package be.tapped.vlaamsetv.auth

import be.tapped.vlaamsetv.ErrorMessage
import kotlinx.coroutines.flow.Flow

interface AuthenticationUseCase {

    suspend fun login(username: String, password: String)

    suspend fun skip()

    val state: Flow<State>

    sealed class State {
        data class Fail(internal val errorMessage: ErrorMessage) : State()
        object Successful : State()
    }
}
