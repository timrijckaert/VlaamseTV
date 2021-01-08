package be.tapped.vlaamsetv.auth

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import be.tapped.vlaamsetv.R

internal class AuthenticationFragment : GuidedStepSupportFragment() {
    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            getString(R.string.vrtnu_title),
            getString(R.string.vrtnu_description),
            getString(R.string.vrtnu_step_breadcrumb),
            ContextCompat.getDrawable(requireContext(), R.drawable.vrt_logo)
        )
    }
}
