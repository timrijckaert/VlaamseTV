package be.tapped.vlaamsetv.auth

import arrow.core.Either

interface TokenUseCase<out T> {

    suspend fun performLogin(
        username: String,
        password: String
    ): Either<T, Unit>

    suspend fun refresh(): Either<T, Boolean>

}
