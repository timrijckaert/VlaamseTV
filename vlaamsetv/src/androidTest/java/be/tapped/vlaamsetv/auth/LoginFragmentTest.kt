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
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.image.KImageView
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.screen.Screen.Companion.onScreen
import io.kotest.matchers.shouldBe
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class LoginFragmentTest {

    private class LoginFragmentScreen : Screen<LoginFragmentScreen>() {
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

    private val testNavHostController =
            TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
                Handler(Looper.getMainLooper()).post {
                    setGraph(R.navigation.authentication_flow_tv)
                    setCurrentDestination(R.id.VRTLoginFragment)
                }
            }

    @Test
    fun noCredentialsArePassed() {
        setupAuthenticationFragment()
        onScreen<LoginFragmentScreen> {
            buttonActionsList {
                getSize() shouldBe 2
                firstChild<LoginFragmentScreen.GuidedActionItem> {
                    title.hasText(R.string.auth_flow_login)
                }
                childAt<LoginFragmentScreen.GuidedActionItem>(1) {
                    title.hasText(R.string.auth_flow_next)
                }
            }
        }
    }

    @Test
    fun nextFocus() {
        setupAuthenticationFragment()
        onScreen<LoginFragmentScreen> {
            guidedActionList {
                getSize() shouldBe 2
                firstChild<LoginFragmentScreen.GuidedActionItem> {
                    click()
                    title.hasText(R.string.auth_flow_email)
                    description {
                        typeText("john.doe@vrt.be")
                        pressImeAction()
                    }
                }
                childAt<LoginFragmentScreen.GuidedActionItem>(1) {
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

    @Test
    fun ifIsLastScreenShouldHaveTheCorrectMessageOnButton() {
        setupAuthenticationFragment(isLastScreen = true)
        onScreen<LoginFragmentScreen> {
            buttonActionsList {
                childAt<LoginFragmentScreen.GuidedActionItem>(1) {
                    title.hasText(R.string.auth_flow_finish)
                }
            }
        }
    }

    @Test
    fun ifIsNotLastScreenShouldHaveTheCorrectMessage() {
        setupAuthenticationFragment(isLastScreen = false)
        onScreen<LoginFragmentScreen> {
            buttonActionsList {
                childAt<LoginFragmentScreen.GuidedActionItem>(1) {
                    title.hasText(R.string.auth_flow_next)
                }
            }
        }

    }

    private fun setupAuthenticationFragment(isLastScreen: Boolean = false) {
        val authenticationUseCase = object : AuthenticationUseCase {
            override suspend fun login(username: String, password: String) {

            }

            override suspend fun skip() {

            }
        }

        launchFragmentInContainer(
                themeResId = R.style.Theme_TV_VlaamseTV,
        ) {
            StubbedLoginFragment(authenticationUseCase, isLastScreen)
                    .also { frag ->
                        frag.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                            if (viewLifecycleOwner != null) {
                                Navigation.setViewNavController(
                                        frag.requireView(),
                                        testNavHostController
                                )
                            }
                        }
                    }
        }
    }
}

class StubbedLoginFragment(
        authenticationUseCase: AuthenticationUseCase,
        private val isLastScreen: Boolean
) :
        LoginFragment(authenticationUseCase) {
    override val config: Configuration
        get() = Configuration(
                R.string.auth_flow_login_title,
                R.string.auth_flow_vrtnu_description,
                R.string.auth_flow_vrtnu_step_breadcrumb,
                R.drawable.vrt_nu_logo,
                isLastScreen,
        )

}
