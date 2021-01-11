package be.tapped.vlaamsetv.auth

import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import be.tapped.vlaamsetv.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class VRTAuthenticationFragment(private val authenticationUseCase: AuthenticationUseCase) :
    GuidedStepSupportFragment() {

    private val navArg by navArgs<VRTAuthenticationFragmentArgs>()
    private val config get() = navArg.config

    @Parcelize
    data class Configuration(
        @StringRes val title: Int,
        @StringRes val description: Int,
        @StringRes val brand: Int,
        @DrawableRes val icon: Int,
        @StringRes val secondaryButtonText: Int,
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
        GuidanceStylist.Guidance(
            getString(config.title),
            getString(config.description),
            getString(config.brand),
            ContextCompat.getDrawable(requireContext(), config.icon)
        )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.addAll(
            listOf(
                GuidedAction.Builder(requireContext())
                    .id(EMAIL_FIELD)
                    .editable(true)
                    .title(R.string.auth_flow_email)
                    .editInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT)
                    .descriptionEditable(true)
                    .build(),
                GuidedAction.Builder(requireContext())
                    .id(PASSWORD_FIELD)
                    .editable(true)
                    .title(R.string.auth_flow_password)
                    .editInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT or InputType.TYPE_MASK_VARIATION)
                    .descriptionEditable(true)
                    .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .build()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            authenticationUseCase.state.collect {
                when (it) {
                    is AuthenticationUseCase.State.Fail -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.auth_flow_fail_dialog_title)
                            .setMessage(it.message)
                            .setNeutralButton(android.R.string.ok) { _, _ -> }
                            .show()
                    }
                    AuthenticationUseCase.State.Successful -> {
                    }
                }
            }
        }
    }

    override fun onCreateButtonActions(
        actions: MutableList<GuidedAction>,
        savedInstanceState: Bundle?
    ) {
        actions.addAll(
            listOf(
                GuidedAction
                    .Builder(context)
                    .id(LOGIN_BUTTON)
                    .title(R.string.auth_flow_login)
                    .build(),
                GuidedAction
                    .Builder(context)
                    .id(SECONDARY_BUTTON)
                    .title(config.secondaryButtonText)
                    .build(),
            )
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        lifecycleScope.launch {
            when (action.id) {
                LOGIN_BUTTON -> authenticationUseCase.login(email, password)
                SECONDARY_BUTTON -> authenticationUseCase.skip()
                else -> super.onGuidedActionClicked(action)
            }
        }
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction): Long =
        when (action.id) {
            EMAIL_FIELD -> PASSWORD_FIELD
            PASSWORD_FIELD ->
                if (hasCredentials) {
                    LOGIN_BUTTON
                } else {
                    SECONDARY_BUTTON
                }
            else -> super.onGuidedActionEditedAndProceed(action)
        }
}
