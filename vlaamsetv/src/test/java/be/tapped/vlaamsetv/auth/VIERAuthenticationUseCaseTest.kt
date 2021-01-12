package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vier.ApiResponse
import be.tapped.vier.profile.HttpProfileRepo
import be.tapped.vlaamsetv.*
import be.tapped.vlaamsetv.prefs.vier.VIERTokenStore
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first

class VIERAuthenticationUseCaseTest : BehaviorSpec() {
    init {
        given("A ${VIERAuthenticationUseCase::class.java.simpleName}") {
            val profileRepo = mockk<HttpProfileRepo>()
            val vierTokenStore = mockk<VIERTokenStore>()
            val authenticationNavigator = mockk<AuthenticationNavigator>()
            val errorMessageConverter = mockk<ErrorMessageConverter<ApiResponse.Failure>>()
            val sut = VIERAuthenticationUseCase(
                profileRepo,
                vierTokenStore,
                authenticationNavigator,
                errorMessageConverter,
            )

            val username = Arb.string().gen()
            val password = Arb.string().gen()
            `when`("logging in") {
                and("a blank username and password was provided") {

                    sut.login("", "")

                    then("it should not make a call") {
                        coVerify(exactly = 0) { profileRepo.fetchTokens("", "") }
                    }

                    then("it should update the state") {
                        sut.state.first() shouldBe AuthenticationUseCase.State.Fail(ErrorMessage(R.string.failure_generic_no_email))
                    }
                }

                val token = vierTokenArb.gen()
                coEvery {
                    profileRepo.fetchTokens(
                        username,
                        password
                    )
                } returns token.right()

                sut.login(username, password)

                and("it was successful") {
                    then("it should save the credentials") {
                        coVerify { vierTokenStore.saveVierCredentials(username, password) }
                    }

                    then("it should save the token") {
                        coVerify { vierTokenStore.saveTokenWrapper(token) }
                    }

                    then("it should have navigated to the next screen") {
                        coVerify { authenticationNavigator.navigateNext() }
                    }

                    then("it should have updated the state") {
                        sut.state.first() shouldBe AuthenticationUseCase.State.Successful
                    }
                }

                and("it was not successful") {
                    val errorMessage = errorMessageArb.gen()
                    every { errorMessageConverter.mapToHumanReadableError(ApiResponse.Failure.HTML.EmptyHTML) } returns errorMessage
                    coEvery {
                        profileRepo.fetchTokens(username, password)
                    } returns ApiResponse.Failure.HTML.EmptyHTML.left()

                    sut.login(username, password)

                    then("it should have updated the state") {
                        sut.state.first() shouldBe AuthenticationUseCase.State.Fail(errorMessage)
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
