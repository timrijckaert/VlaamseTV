package be.tapped.vlaamsetv.browse

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DividerRow
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.PageRow
import androidx.leanback.widget.SectionRow
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vlaamsetv.browse.presenter.PresenterSelector
import be.tapped.vlaamsetv.browse.vrt.ProgramMapper
import be.tapped.vlaamsetv.browse.vrt.VRTBrowseUseCase
import be.tapped.vlaamsetv.browse.vrt.VRTNUAZUseCase
import be.tapped.vlaamsetv.browse.vrt.VRTNUAZUseCaseImpl
import be.tapped.vrtnu.content.DefaultScreenshotRepo
import be.tapped.vrtnu.content.LiveStreams
import be.tapped.vrtnu.content.ScreenshotRepo
import be.tapped.vrtnu.content.VRTApi
import kotlinx.coroutines.launch

class VRTAZFragment(
    private val backgroundManager: BackgroundManager,
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
    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> =
        BrowseSupportFragment.MainFragmentAdapter(this)
}

class BrowseFragment(private val backgroundManager: BackgroundManager) : BrowseSupportFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainFragmentRegistry.registerFragment(
            PageRow::class.java,
            object : BrowseSupportFragment.FragmentFactory<Fragment>() {
                override fun createFragment(row: Any?): Fragment {
                    return VRTAZFragment(backgroundManager, VRTBrowseUseCase(VRTNUAZUseCaseImpl(VRTApi(), ProgramMapper())))
                }
            },
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareEntranceTransition()
        adapter = ArrayObjectAdapter(ListRowPresenter()).apply {
            val vrtSection = SectionRow(HeaderItem("VRT NU"))

            val liveStreamObjectAdapter = ArrayObjectAdapter(PresenterSelector()).apply {
                LiveStreams.allLiveStreams.forEachIndexed { index, it ->
                    val screenGrab = when (it.brand) {
                        LiveStreams.LiveStream.Brand.EEN -> DefaultScreenshotRepo.screenshotForBrand(ScreenshotRepo.Brand.EEN)
                        LiveStreams.LiveStream.Brand.CANVAS -> DefaultScreenshotRepo.screenshotForBrand(ScreenshotRepo.Brand.CANVAS)
                        LiveStreams.LiveStream.Brand.KETNET -> DefaultScreenshotRepo.screenshotForBrand(ScreenshotRepo.Brand.KETNET)
                        LiveStreams.LiveStream.Brand.KETNET_JUNIOR,
                        LiveStreams.LiveStream.Brand.SPORZA,
                        LiveStreams.LiveStream.Brand.VRT_NWS,
                        LiveStreams.LiveStream.Brand.RADIO_1,
                        LiveStreams.LiveStream.Brand.RADIO_2,
                        LiveStreams.LiveStream.Brand.KLARA,
                        LiveStreams.LiveStream.Brand.STUDIO_BRUSSEL,
                        LiveStreams.LiveStream.Brand.MNM,
                        LiveStreams.LiveStream.Brand.VRT_NXT,
                        -> null
                    }
                    add(Item.ImageCard(index, it.name, imageViewUrl = screenGrab))
                }
            }

            val vrtLiveStreams = ListRow(HeaderItem(0L, "Live TV"), liveStreamObjectAdapter)
            val vrtAZPrograms = PageRow(HeaderItem(1L, "All Programs"))
            val divider = DividerRow()

            addAll(0, listOf(vrtSection, vrtLiveStreams, vrtAZPrograms, divider))
        }
    }
}

