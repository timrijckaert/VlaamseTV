package be.tapped.vlaamsetv.browse

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DividerRow
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.PageRow
import androidx.leanback.widget.SectionRow
import androidx.lifecycle.lifecycleScope
import be.tapped.vrtnu.content.VRTApi
import kotlinx.coroutines.launch
import androidx.leanback.widget.VerticalGridPresenter
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vlaamsetv.browse.presenter.PresenterSelector

class VRTAZFragment : VerticalGridSupportFragment(),
                      BrowseSupportFragment.MainFragmentAdapterProvider {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gridPresenter = VerticalGridPresenter().apply {
            numberOfColumns = 5
        }
        val arrayAdapter = ArrayObjectAdapter(PresenterSelector())
        adapter = arrayAdapter

        val vrtApi = VRTApi()
        lifecycleScope.launch {
            val vrtPrograms = vrtApi.fetchAZPrograms().orNull()?.programs ?: emptyList()
            vrtPrograms.map {
                Item.ImageCard(
                    it.title,
                    it.description,
                    it.thumbnail
                )
            }.forEach(arrayAdapter::add)
        }
    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> =
        BrowseSupportFragment.MainFragmentAdapter(this)
}

class BrowseFragment : BrowseSupportFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainFragmentRegistry.registerFragment(
            PageRow::class.java,
            object : BrowseSupportFragment.FragmentFactory<Fragment>() {
                override fun createFragment(row: Any?): Fragment = VRTAZFragment()
            },
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareEntranceTransition()
        adapter = ArrayObjectAdapter(ListRowPresenter()).apply {
            val vrtSection = SectionRow(HeaderItem("VRT NU"))
            val vrtAZPrograms = PageRow(HeaderItem(0L, "AZ"))
            val divider = DividerRow()

            addAll(0, listOf(vrtSection, vrtAZPrograms, divider))
        }
    }
}

