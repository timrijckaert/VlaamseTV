package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.*
import be.tapped.vlaamsetv.prefs.vtm.VTMTokenStore
import be.tapped.vtmgo.ApiResponse
import be.tapped.vtmgo.profile.HttpProfileRepo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.*

class VTMAuthenticationUseCaseTest : BehaviorSpec() {
    init {
        given("A ${VTMAuthenticationUseCase::class.java.simpleName}") {
            val profileRepo = mockk<HttpProfileRepo>()
            val vtmTokenStore = mockk<VTMTokenStore>()
            val authenticationNavigator = mockk<AuthenticationNavigator>()
            val errorMessageConverter = mockk<ErrorMessageConverter<ApiResponse.Failure>>()
            val sut = VTMAuthenticationUseCase(
                    profileRepo,
                    vtmTokenStore,
                    authenticationNavigator,
                    errorMessageConverter,
            )

            val username = Arb.string().gen()
            val password = Arb.string().gen()
            `when`("logging in") {
                and("a blank username and password was provided") {

                    sut.login("", "")

                    then("it should not make a call") {
                        coVerify(exactly = 0) { profileRepo.login("", "") }
                    }

                    then("it should navigate to the error screen") {
                        verify { authenticationNavigator.navigateToErrorScreen(ErrorMessage(R.string.failure_generic_no_email)) }
                    }
                }

                val token = ApiResponse.Success.Authentication.Token(vtmTokenWrapper.gen())
                coEvery {
                    profileRepo.login(
                            username,
                            password
                    )
                } returns token.right()

                sut.login(username, password)

                and("it was successful") {
                    then("it should save the credentials") {
                        coVerify { vtmTokenStore.saveVTMCredentials(username, password) }
                    }

                    then("it should save the JWT token") {
                        coVerify { vtmTokenStore.saveToken(token.token) }
                    }

                    then("it should have navigated to the next screen") {
                        coVerify { authenticationNavigator.navigateNext() }
                    }
                }

                and("it was not successful") {
                    val errorMessage = errorMessageArb.gen()
                    every { errorMessageConverter.mapToHumanReadableError(ApiResponse.Failure.EmptyJson) } returns errorMessage
                    coEvery {
                        profileRepo.login(username, password)
                    } returns ApiResponse.Failure.EmptyJson.left()

                    sut.login(username, password)

                    then("it should navigate to the error screen") {
                        verify {
                            authenticationNavigator.navigateToErrorScreen(errorMessage)
                        }
                    }
                }
            }

            `when`("skipping") {
                sut.skip()

                then("it should navigate to the next screen") {
                    coVerify { authenticationNavigator.navigateNext() }
                }
            }
        }
    }
}
