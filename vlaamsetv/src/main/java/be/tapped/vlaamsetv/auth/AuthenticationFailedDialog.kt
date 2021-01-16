package be.tapped.vlaamsetv.auth

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.navigation.fragment.navArgs
import be.tapped.vlaamsetv.R

class AuthenticationFailedDialog(private val authenticationNavigator: AuthenticationNavigator) : GuidedStepSupportFragment() {

    private val navArgs by navArgs<AuthenticationFailedDialogArgs>()

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance(
            requireContext().getString(R.string.auth_flow_fail_dialog_title),
            navArgs.errorMessage,
            requireContext().getString(R.string.auth_flow_login_title),
            null
        )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(requireContext()).clickAction(GuidedAction.ACTION_ID_CONTINUE).build(),
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {
        authenticationNavigator.navigateBack()
    }
}
