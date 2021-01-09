package be.tapped.vlaamsetv.auth

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class VRTAuthenticationFragment(private val vrtAuthenticationUseCase: AuthenticationUseCase) :
    GuidedStepSupportFragment() {

    companion object {
        private const val EMAIL_FIELD = 1L
        private const val PASSWORD_FIELD = 2L
        private const val LOGIN_BUTTON = 3L
        private const val SKIP_BUTTON = 4L
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance(
            getString(R.string.auth_flow_vrtnu_title),
            getString(R.string.auth_flow_vrtnu_description),
            getString(R.string.auth_flow_vrtnu_step_breadcrumb),
            ContextCompat.getDrawable(requireContext(), R.drawable.vrt_nu_logo)
        )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.addAll(
            listOf(
                GuidedAction.Builder(requireContext())
                    .id(EMAIL_FIELD)
                    .editable(true)
                    .title(R.string.auth_flow_email)
                    .editInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    .descriptionEditable(true)
                    .build(),
                GuidedAction.Builder(requireContext())
                    .id(PASSWORD_FIELD)
                    .editable(true)
                    .title(R.string.auth_flow_password)
                    .editInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .descriptionEditable(true)
                    .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .build()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            vrtAuthenticationUseCase.state.collect {
                when (it) {
                    AuthenticationUseCase.State.Empty -> {
                    }
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
                    .id(SKIP_BUTTON)
                    .title(R.string.auth_flow_skip)
                    .build(),
            )
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        lifecycleScope.launch {
            when (action.id) {
                LOGIN_BUTTON -> {
                    vrtAuthenticationUseCase.login()
                }
                SKIP_BUTTON -> {
                    vrtAuthenticationUseCase.skip()
                }
                else -> super.onGuidedActionClicked(action)
            }
        }
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction): Long =
        when (action.id) {
            EMAIL_FIELD -> {
                vrtAuthenticationUseCase.credentials =
                    vrtAuthenticationUseCase.credentials.copy(username = action.description.toString())
                PASSWORD_FIELD
            }
            PASSWORD_FIELD -> {
                vrtAuthenticationUseCase.credentials =
                    vrtAuthenticationUseCase.credentials.copy(password = action.description.toString())

                if (vrtAuthenticationUseCase.credentials.allFieldsAreFilledIn) {
                    LOGIN_BUTTON
                } else {
                    SKIP_BUTTON
                }
            }
            else -> super.onGuidedActionEditedAndProceed(action)
        }
}
