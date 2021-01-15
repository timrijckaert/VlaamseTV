package be.tapped.vlaamsetv.auth

interface AuthenticationUIController {

    suspend fun login(username: String, password: String)

    suspend fun skip()

}
