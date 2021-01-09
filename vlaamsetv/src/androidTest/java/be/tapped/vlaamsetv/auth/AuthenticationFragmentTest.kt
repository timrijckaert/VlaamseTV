package be.tapped.vlaamsetv.auth

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.tapped.vlaamsetv.R
import com.agoda.kakao.check.KCheckBox
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.image.KImageView
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.screen.Screen.Companion.onScreen
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

        class GuidedActionItem(parent: Matcher<View>) : KRecyclerItem<GuidedActionItem>(parent) {
            val checkMark = KCheckBox(parent) { withId(R.id.guidedactions_item_checkmark) }
            val icon = KImageView(parent) { withId(R.id.guidedactions_item_icon) }
            val title = KEditText(parent) { withId(R.id.guidedactions_item_title) }
            val description = KEditText(parent) { withId(R.id.guidedactions_item_description) }
            val chevron = KImageView(parent) { withId(R.id.guidedactions_item_chevron) }
        }
    }

    @Test
    internal fun authenticationFlowNextFocus() {
        launchFragmentInContainer<AuthenticationFragment>(themeResId = R.style.Theme_TV_VlaamseTV)
        onScreen<AuthenticationFragmentScreen> {
            guidedActionList {
                firstChild<AuthenticationFragmentScreen.GuidedActionItem> {
                    title {
                        typeText("john.doe@gmail.com")
                    }
                }
                childAt<AuthenticationFragmentScreen.GuidedActionItem>(1) {
                    isFocused()
                    title {
                        typeText("mysupersecretpassword")
                        pressImeAction()
                    }
                }
            }
            buttonActionsList {
                firstChild<AuthenticationFragmentScreen.GuidedActionItem> {
                    isFocused()
                }
            }
        }
    }
}
