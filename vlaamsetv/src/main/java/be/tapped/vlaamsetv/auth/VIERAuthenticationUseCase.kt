package be.tapped.vlaamsetv.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class VIERAuthenticationUseCase : AuthenticationUseCase {

    override suspend fun login(username: String, password: String) {

    }

    override suspend fun skip() {

    }

    private val _state: MutableSharedFlow<AuthenticationUseCase.State> = MutableSharedFlow(1)

    override val state: Flow<AuthenticationUseCase.State> get() = _state.asSharedFlow()
}
