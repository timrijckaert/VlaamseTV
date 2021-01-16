package be.tapped.vlaamsetv.auth

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.ErrorMessage
import be.tapped.vlaamsetv.R

interface TokenUseCase {

    fun checkPreconditions(
        username: String,
        password: String
    ): Either<ErrorMessage, Unit> {
        if (username.isBlank()) {
            return ErrorMessage(R.string.failure_generic_no_email).left()
        }

        if (password.isBlank()) {
            return ErrorMessage(R.string.failure_generic_no_password).left()
        }

        return Unit.right()
    }

    suspend fun hasCredentials(): Boolean

    suspend fun performLogin(username: String, password: String): Either<ErrorMessage, Unit>

    suspend fun refresh(): Either<ErrorMessage, Boolean>

}
