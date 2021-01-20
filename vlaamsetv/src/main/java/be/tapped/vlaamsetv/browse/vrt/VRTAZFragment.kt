package be.tapped.vlaamsetv.browse.vrt

import android.os.Bundle
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.browse.BrowseNavigator
import be.tapped.vlaamsetv.browse.presenter.PresenterSelector
import kotlinx.coroutines.launch

class VRTAZFragment(
    private val backgroundManager: BackgroundManager,
    private val browseNavigator: BrowseNavigator,
    private val azUseCase: VRTNUAZUseCase,
) : VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider {

    private companion object {

        private const val NUMBER_OF_COLUMNS = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gridPresenter = VerticalGridPresenter().apply {
            numberOfColumns = NUMBER_OF_COLUMNS
        }

        val arrayAdapter = ArrayObjectAdapter(PresenterSelector())
        adapter = arrayAdapter
        lifecycleScope.launch {
            arrayAdapter.addAll(0, azUseCase.fetchAZPrograms())
        }

        setOnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
            browseNavigator.navigateToDetail()
        }
    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> =
        BrowseSupportFragment.MainFragmentAdapter(this)
}
