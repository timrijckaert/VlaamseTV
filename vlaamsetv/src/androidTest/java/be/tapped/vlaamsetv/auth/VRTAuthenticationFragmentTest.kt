package be.tapped.vlaamsetv.auth

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class VRTAuthenticationFragmentTest {

    private class VRTAuthenticationFragmentScreen : Screen<VRTAuthenticationFragmentScreen>() {
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
    internal fun authenticationFailedShouldShowDialog() {
        launchFragmentInContainer(themeResId = R.style.Theme_TV_VlaamseTV) {
            VRTAuthenticationFragment(object : AuthenticationUseCase {
                override suspend fun login() {
                    _state.value = AuthenticationUseCase.State.Fail("Failed to login")
                }

                override suspend fun skip() {}

                override var credentials: AuthenticationUseCase.Credentials =
                    AuthenticationUseCase.Credentials(AuthenticationUseCase.Brand.VRT_NU)
                private val _state: MutableStateFlow<AuthenticationUseCase.State> =
                    MutableStateFlow(AuthenticationUseCase.State.Empty)
                override val state: StateFlow<AuthenticationUseCase.State> get() = _state
            })
        }
        onScreen<VRTAuthenticationFragmentScreen> {
            buttonActionsList {
                firstChild<VRTAuthenticationFragmentScreen.GuidedActionItem> {
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
    internal fun authenticationWasSuccessfulShouldDo() {
        launchFragmentInContainer(themeResId = R.style.Theme_TV_VlaamseTV) {
            VRTAuthenticationFragment(object : AuthenticationUseCase {
                override suspend fun login() {
                    _state.value = AuthenticationUseCase.State.Successful
                }

                override suspend fun skip() {}

                override var credentials: AuthenticationUseCase.Credentials =
                    AuthenticationUseCase.Credentials(AuthenticationUseCase.Brand.VRT_NU)
                private val _state: MutableStateFlow<AuthenticationUseCase.State> =
                    MutableStateFlow(AuthenticationUseCase.State.Empty)
                override val state: StateFlow<AuthenticationUseCase.State> get() = _state
            })
        }
        onScreen<VRTAuthenticationFragmentScreen> {
            buttonActionsList {
                firstChild<VRTAuthenticationFragmentScreen.GuidedActionItem> {
                    click()
                }
            }
        }
    }
}
