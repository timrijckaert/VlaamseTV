package be.tapped.vlaamsetv.browse

import android.os.Bundle
import android.view.View
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DividerRow
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.SectionRow
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.browse.presenter.PresenterSelector
import be.tapped.vlaamsetv.browse.vrt.LiveTVUseCaseImpl
import be.tapped.vlaamsetv.browse.vrt.VRTBrowseUseCase
import be.tapped.vlaamsetv.browse.vrt.VRTNUAZUseCaseImpl
import be.tapped.vrtnu.content.VRTApi
import kotlinx.coroutines.launch

class BrowseFragment(private val backgroundManager: BackgroundManager) : BrowseSupportFragment() {

    private val vrtBrowseUseCase = VRTBrowseUseCase(
        VRTNUAZUseCaseImpl(VRTApi()),
        LiveTVUseCaseImpl(),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareEntranceTransition()
        lifecycleScope.launch {
            adapter = ArrayObjectAdapter(ListRowPresenter().apply { setNumRows(2) }).apply {
                val vrtSection = SectionRow(HeaderItem(view.context.getString(R.string.vrt_nu_name)))

                val vrtLiveStreams = ListRow(HeaderItem(0L, view.context.getString(R.string.vrt_nu_live_tv)),
                    ArrayObjectAdapter(PresenterSelector()).apply {
                        addAll(0, vrtBrowseUseCase.liveStreams())
                    })
                val vrtAZPrograms = ListRow(HeaderItem(1L, view.context.getString(R.string.vrt_nu_all_programs)),
                    ArrayObjectAdapter(PresenterSelector()).apply {
                        addAll(0, vrtBrowseUseCase.fetchAZPrograms())
                    })
                val divider = DividerRow()

                addAll(0, listOf(vrtSection, vrtLiveStreams, vrtAZPrograms, divider))
            }
        }
    }
}

