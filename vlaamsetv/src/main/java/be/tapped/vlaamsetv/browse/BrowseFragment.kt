package be.tapped.vlaamsetv.browse

import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vlaamsetv.browse.vrt.CategoriesUseCaseImpl
import be.tapped.vlaamsetv.browse.vrt.LiveTVUseCaseImpl
import be.tapped.vlaamsetv.browse.vrt.MostRecentUseCaseImpl
import be.tapped.vlaamsetv.browse.vrt.VRTBrowseUseCase
import be.tapped.vlaamsetv.browse.vrt.VRTNUAZUseCaseImpl
import be.tapped.vrtnu.content.VRTApi
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BrowseFragment(
    private val backgroundManager: BackgroundManager,
    private val browseNavigator: BrowseNavigator
) : BrowseSupportFragment() {

    private val vrtApi = VRTApi()
    private val vrtBrowseUseCase = VRTBrowseUseCase(
        VRTNUAZUseCaseImpl(vrtApi),
        LiveTVUseCaseImpl(),
        CategoriesUseCaseImpl(vrtApi),
        MostRecentUseCaseImpl(vrtApi),
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareEntranceTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = view.context
        lifecycleScope.launch {
            adapter = ArrayObjectAdapter(ListRowPresenter().apply { setNumRows(2) }).apply {
                addAll(0, vrtBrowseUseCase.constructMenu(ctx))
                //add(DividerRow())
                startEntranceTransition()
            }
        }

        var backgroundImageDownloadJob: Job? = null
        setOnItemViewSelectedListener { itemViewHolder, rawItem, rowViewHolder, row ->
            backgroundImageDownloadJob?.cancel()
            rawItem?.let {
                val background = when (val item = (it as Item)) {
                    is Item.ImageCard -> item.background
                }
                if (background == null) {
                    backgroundManager.clearDrawable()
                } else {
                    backgroundImageDownloadJob = lifecycleScope.launch {
                        backgroundManager.setBitmap(
                            ctx.imageLoader.execute(
                                ImageRequest.Builder(ctx)
                                    .data(background)
                                    .build()
                            ).drawable?.toBitmap()
                        )
                    }
                }
            }
        }

        setOnItemViewClickedListener { _, untypedItem, _, _ ->
            check(untypedItem is Item) { "$untypedItem was not of type ${Item::class}!" }
            browseNavigator.navigateToDetail(untypedItem as Item)
        }
    }
}
