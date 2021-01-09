package be.tapped.vlaamsetv.auth

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import be.tapped.vlaamsetv.R

internal class AuthenticationFragment : GuidedStepSupportFragment() {

    companion object {
        private const val EMAIL_FIELD = 1L
        private const val PASSWORD_FIELD = 2L
        private const val LOGIN_BUTTON = 3L
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
                    .title(R.string.auth_flow_skip)
                    .build(),
            )
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {
        super.onGuidedActionClicked(action)
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction): Long =
        when (action.id) {
            EMAIL_FIELD -> PASSWORD_FIELD
            PASSWORD_FIELD -> LOGIN_BUTTON
            else -> super.onGuidedActionEditedAndProceed(action)
        }
}
