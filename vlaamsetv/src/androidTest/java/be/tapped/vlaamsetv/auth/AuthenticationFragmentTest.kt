package be.tapped.vlaamsetv.auth

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.tapped.vlaamsetv.R
import com.agoda.kakao.check.KCheckBox
import com.agoda.kakao.dialog.KAlertDialog
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.image.KImageView
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.screen.Screen.Companion.onScreen
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class AuthenticationFragmentTest {

    private class AuthenticationFragmentScreen : Screen<AuthenticationFragmentScreen>() {
        val guidedActionList =
            KRecyclerView(
                builder = { withId(R.id.guidedactions_list) },
                itemTypeBuilder = { itemType(::GuidedActionItem) })

        val buttonActionsList =
            KRecyclerView(
                builder = { withId(R.id.guidedactions_list2) },
                itemTypeBuilder = { itemType(::GuidedActionItem) })

        val alertDialog = KAlertDialog()

        class GuidedActionItem(parent: Matcher<View>) : KRecyclerItem<GuidedActionItem>(parent) {
            val checkMark = KCheckBox(parent) { withId(R.id.guidedactions_item_checkmark) }
            val icon = KImageView(parent) { withId(R.id.guidedactions_item_icon) }
            val title = KEditText(parent) { withId(R.id.guidedactions_item_title) }
            val description = KEditText(parent) { withId(R.id.guidedactions_item_description) }
            val chevron = KImageView(parent) { withId(R.id.guidedactions_item_chevron) }
        }
    }

    @Test
    fun noCredentialsArePassed() {
        setupVRTAuthenticationFragment()
        onScreen<AuthenticationFragmentScreen> {
            buttonActionsList {
                getSize() shouldBe 2
                firstChild<AuthenticationFragmentScreen.GuidedActionItem> {
                    title.hasText(R.string.auth_flow_login)
                }
                childAt<AuthenticationFragmentScreen.GuidedActionItem>(1) {
                    title.hasText(R.string.auth_flow_skip)
                }
            }
        }
    }

    @Test
    fun nextFocus() {
        setupVRTAuthenticationFragment(login = { _, _ -> AuthenticationUseCase.State.Empty })
        onScreen<AuthenticationFragmentScreen> {
            guidedActionList {
                getSize() shouldBe 2
                firstChild<AuthenticationFragmentScreen.GuidedActionItem> {
                    click()
                    title.hasText(R.string.auth_flow_email)
                    description {
                        typeText("john.doe@vrt.be")
                        pressImeAction()
                    }
                }
                childAt<AuthenticationFragmentScreen.GuidedActionItem>(1) {
                    isFocused()
                    title.hasText(R.string.auth_flow_password)
                    description {
                        typeText("my-super-secret-password")
                        pressImeAction()
                    }
                }
            }
        }
    }

    private val testNavHostController =
        TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
            Handler(Looper.getMainLooper()).post {
                setGraph(R.navigation.authentication_flow_tv)
                setCurrentDestination(R.id.authenticationFragment)
            }
        }

    @Test
    internal fun authenticationWasSuccessfulShouldDo() {
        setupVRTAuthenticationFragment(login = { _, _ -> AuthenticationUseCase.State.Successful })
        onScreen<AuthenticationFragmentScreen> {
            buttonActionsList {
                firstChild<AuthenticationFragmentScreen.GuidedActionItem> {
                    click()
                }
            }
        }
    }

    @Test
    internal fun authenticationFailedShouldShowDialog() {
        setupVRTAuthenticationFragment(login = { _, _ -> AuthenticationUseCase.State.Fail("Failed to login") })
        onScreen<AuthenticationFragmentScreen> {
            buttonActionsList {
                firstChild<AuthenticationFragmentScreen.GuidedActionItem> {
                    click()
                }
            }

            alertDialog {
                isDisplayed()
                title.hasText(R.string.auth_flow_fail_dialog_title)
                message.hasText("Failed to login")
            }
        }
    }

    @Test
    fun passingCredentials() {
        var output = ""
        setupVRTAuthenticationFragment(
            login = { username, pass ->
                output = "$username-$pass"
                AuthenticationUseCase.State.Empty
            }
        )
        onScreen<AuthenticationFragmentScreen> {
            guidedActionList {
                firstChild<AuthenticationFragmentScreen.GuidedActionItem> {
                    click()
                    description {
                        typeText("john.doe@vrt.be")
                        pressImeAction()
                    }
                }
                childAt<AuthenticationFragmentScreen.GuidedActionItem>(1) {
                    description {
                        typeText("my-super-secret-password")
                        pressImeAction()
                    }
                }
            }
            buttonActionsList {
                firstChild<AuthenticationFragmentScreen.GuidedActionItem> {
                    click()
                }
            }
        }
        output.toLowerCase() shouldBe "john.doe@vrt.be-my-super-secret-password"
    }

    @Test
    fun enteringWrongCredentialsTwiceShouldRetriggerAlertDialog() {
        setupVRTAuthenticationFragment(login = { _, _ -> AuthenticationUseCase.State.Fail("Failed to login") })
        onScreen<AuthenticationFragmentScreen> {
            buttonActionsList {
                firstChild<AuthenticationFragmentScreen.GuidedActionItem> {
                    click()
                }
            }

            alertDialog {
                isDisplayed()
                title.hasText(R.string.auth_flow_fail_dialog_title)
                message.hasText("Failed to login")
                neutralButton.click()
            }

            buttonActionsList {
                firstChild<AuthenticationFragmentScreen.GuidedActionItem> {
                    click()
                }
            }

            alertDialog {
                isDisplayed()
                title.hasText(R.string.auth_flow_fail_dialog_title)
                message.hasText("Failed to login")
            }
        }
    }

    private fun setupVRTAuthenticationFragment(
        login: ((username: String, password: String) -> AuthenticationUseCase.State)? = null,
        skip: (() -> AuthenticationUseCase.State)? = null,
        initialState: AuthenticationUseCase.State = AuthenticationUseCase.State.Empty
    ) {
        launchFragmentInContainer(
            themeResId = R.style.Theme_TV_VlaamseTV,
            fragmentArgs = AuthenticationFragmentArgs(
                AuthenticationFragment.Configuration(
                    R.string.auth_flow_vrtnu_title,
                    R.string.auth_flow_vrtnu_description,
                    R.string.auth_flow_vrtnu_step_breadcrumb,
                    R.drawable.vrt_nu_logo
                )
            ).toBundle()
        ) {
            AuthenticationFragment(object : AuthenticationUseCase {
                override suspend fun login(username: String, password: String) {
                    if (login != null) {
                        _state.value = login(username, password)
                    }
                }

                override suspend fun skip() {
                    if (skip != null) {
                        _state.value = skip()
                    }
                }

                private val _state: MutableStateFlow<AuthenticationUseCase.State> =
                    MutableStateFlow(initialState)
                override val state: StateFlow<AuthenticationUseCase.State> get() = _state
            }).also { frag ->
                frag.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        Navigation.setViewNavController(frag.requireView(), testNavHostController)
                    }
                }
            }
        }
    }
}
