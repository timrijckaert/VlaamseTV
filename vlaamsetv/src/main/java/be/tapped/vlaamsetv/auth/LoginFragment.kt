package be.tapped.vlaamsetv.auth

import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import be.tapped.vlaamsetv.R
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
data class DefaultLoginConfiguration(val isLastScreen: Boolean) : Parcelable

abstract class LoginFragment(private val authenticationUIController: AuthenticationUIController) : GuidedStepSupportFragment() {

    abstract val config: Configuration

    @Parcelize
    data class Configuration(
        @StringRes val title: Int,
        @StringRes val description: Int,
        @StringRes val brand: Int,
        @DrawableRes val icon: Int,
        val isLastScreen: Boolean,
    ) : Parcelable

    companion object {

        private const val EMAIL_FIELD = 1L
        private const val PASSWORD_FIELD = 2L
        private const val LOGIN_BUTTON = 3L
        private const val SECONDARY_BUTTON = 4L
    }

    private val email get() = findActionById(EMAIL_FIELD).description?.toString() ?: ""
    private val hasEmail get() = email.isNotBlank()
    private val password get() = findActionById(PASSWORD_FIELD).description?.toString() ?: ""
    private val hasPassword get() = password.isNotBlank()
    private val hasCredentials get() = hasEmail && hasPassword

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance(getString(config.title),
            getString(config.description),
            getString(config.brand),
            ContextCompat.getDrawable(requireContext(), config.icon))

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.addAll(
            listOf(
                GuidedAction
                    .Builder(requireContext())
                    .id(EMAIL_FIELD)
                    .title(R.string.auth_flow_email)
                    .editInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT)
                    .descriptionEditable(true)
                    .build(),
                GuidedAction
                    .Builder(requireContext())
                    .id(PASSWORD_FIELD)
                    .title(R.string.auth_flow_password)
                    .descriptionInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT)
                    .descriptionEditable(true)
                    .build()
            )
        )
    }

    override fun onCreateButtonActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.addAll(
            listOf(
                GuidedAction.Builder(context).id(LOGIN_BUTTON).title(R.string.auth_flow_login).build(),
                GuidedAction
                    .Builder(context)
                    .id(SECONDARY_BUTTON)
                    .title(if (config.isLastScreen) R.string.auth_flow_finish else R.string.auth_flow_next)
                    .build(),
            )
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        lifecycleScope.launch {
            when (action.id) {
                LOGIN_BUTTON -> authenticationUIController.login(email, password)
                SECONDARY_BUTTON -> authenticationUIController.next()
                else             -> super.onGuidedActionClicked(action)
            }
        }
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction): Long = when (action.id) {
        EMAIL_FIELD -> PASSWORD_FIELD
        PASSWORD_FIELD -> if (hasCredentials) {
            LOGIN_BUTTON
        } else {
            SECONDARY_BUTTON
        }
        else           -> super.onGuidedActionEditedAndProceed(action)
    }
}

class VRTLoginFragment(vrtAuthenticationUseCase: VRTAuthenticationUIController) : LoginFragment(vrtAuthenticationUseCase) {

    private val navArg by navArgs<VRTLoginFragmentArgs>()
    override val config: Configuration
        get() = Configuration(
            R.string.auth_flow_login_title,
            R.string.auth_flow_vrtnu_description,
            R.string.auth_flow_vrtnu_step_breadcrumb,
            R.drawable.vrt_nu_logo,
            navArg.config.isLastScreen,
        )
}

class VTMLoginFragment(vtmAuthenticationUseCase: VTMAuthenticationUIController) : LoginFragment(vtmAuthenticationUseCase) {

    private val navArg by navArgs<VTMLoginFragmentArgs>()

    override val config: Configuration
        get() = Configuration(
            R.string.auth_flow_login_title,
            R.string.auth_flow_vtmgo_description,
            R.string.auth_flow_vtmgo_step_breadcrumb,
            R.drawable.vtm_logo,
            navArg.config.isLastScreen,
        )
}

class VIERLoginFragment(vierAuthenticationUseCase: VIERAuthenticationUIController) : LoginFragment(vierAuthenticationUseCase) {

    private val navArg by navArgs<VIERLoginFragmentArgs>()

    override val config: Configuration
        get() = Configuration(
            R.string.auth_flow_login_title,
            R.string.auth_flow_vier_description,
            R.string.auth_flow_vier_step_breadcrumb,
            R.drawable.vier_logo,
            navArg.config.isLastScreen,
        )
}
