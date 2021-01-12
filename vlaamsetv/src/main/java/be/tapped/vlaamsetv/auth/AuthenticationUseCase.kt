package be.tapped.vlaamsetv.auth

interface AuthenticationUseCase {

    suspend fun login(username: String, password: String)

    suspend fun skip()

}
