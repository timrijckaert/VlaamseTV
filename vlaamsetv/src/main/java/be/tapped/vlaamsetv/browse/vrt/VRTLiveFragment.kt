package be.tapped.vlaamsetv.browse.vrt

import android.os.Bundle
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.browse.presenter.PresenterSelector
import kotlinx.coroutines.launch

class VRTLiveFragment(private val backgroundManager: BackgroundManager, private val liveTVUseCase: LiveTVUseCase) :
    RowsSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mRowsAdapter = ArrayObjectAdapter(ListRowPresenter().apply { setNumRows(3) })
        adapter = mRowsAdapter
        lifecycleScope.launch {
            mRowsAdapter.add(
                ListRow(ArrayObjectAdapter(PresenterSelector()).apply {
                    addAll(0, liveTVUseCase.liveStreams())
                })
            )
        }
    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> =
        BrowseSupportFragment.MainFragmentAdapter(this)
}
