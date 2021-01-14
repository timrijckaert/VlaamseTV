package be.tapped.vlaamsetv.auth

import arrow.core.left
import arrow.core.right
import be.tapped.vlaamsetv.*
import be.tapped.vlaamsetv.prefs.vrt.VRTTokenStore
import be.tapped.vrtnu.ApiResponse
import be.tapped.vrtnu.profile.TokenRepo
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.mockk.*

class VRTAuthenticationUseCaseTest : BehaviorSpec({

    given("A ${VRTAuthenticationUseCase::class.simpleName}") {
        val tokenRepo = mockk<TokenRepo>()
        val vrtTokenStore = mockk<VRTTokenStore>()
        val authenticationNavigator = mockk<AuthenticationNavigator>()
        val errorMessageConverter = mockk<VRTErrorMessageConverter>()
        val sut = VRTAuthenticationUseCase(
            tokenRepo,
            vrtTokenStore,
            authenticationNavigator,
            errorMessageConverter
        )

        val username = Arb.string().gen()
        val password = Arb.string().gen()
        `when`("logging in") {
            and("a blank username and password was provided") {

                sut.login("", "")

                then("it should not make a call") {
                    coVerify(exactly = 0) { tokenRepo.fetchTokenWrapper("", "") }
                }

                then("it should navigate to the error screen") {
                    verify { authenticationNavigator.navigateToErrorScreen(ErrorMessage(R.string.failure_generic_no_email)) }
                }
            }

            val tokenWrapper = vrtTokenWrapperArb.gen()
            val xVRTToken = xVRTTokenArb.gen()

            coEvery {
                tokenRepo.fetchTokenWrapper(username, password)
            } returns ApiResponse.Success.Authentication.Token(tokenWrapper).right()

            coEvery {
                tokenRepo.fetchXVRTToken(username, password)
            } returns ApiResponse.Success.Authentication.VRTToken(xVRTToken).right()

            sut.login(username, password)

            and("it was successful") {
                then("it should save the VRT NU credentials") {
                    coVerify { vrtTokenStore.saveVRTCredentials(username, password) }
                }

                then("it should save the retrieved token wrapper") {
                    coVerify { vrtTokenStore.saveTokenWrapper(tokenWrapper) }
                }

                then("it should save the XVRT token") {
                    coVerify { vrtTokenStore.saveXVRTToken(xVRTToken) }
                }

                then("it should have navigated to the next screen") {
                    coVerify { authenticationNavigator.navigateNext() }
                }
            }

            and("it was not successful") {
                val errorMessage = errorMessageArb.gen()
                every { errorMessageConverter.mapToHumanReadableError(ApiResponse.Failure.EmptyJson) } returns errorMessage
                coEvery {
                    tokenRepo.fetchTokenWrapper(username, password)
                } returns ApiResponse.Failure.EmptyJson.left()

                sut.login(username, password)

                then("it should have updated the state") {
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
})

