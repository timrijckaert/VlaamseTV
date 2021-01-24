package be.tapped.vlaamsetv.browse

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.SectionRow
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vlaamsetv.browse.presenter.PresenterSelector
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

class BrowseFragment(private val backgroundManager: BackgroundManager) : BrowseSupportFragment() {

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
                add(SectionRow(HeaderItem(ctx.getString(R.string.vrt_nu_name))))

                add(
                    ListRow(
                        HeaderItem(ctx.getString(R.string.vrt_nu_live_tv)),
                        ArrayObjectAdapter(PresenterSelector()).apply {
                            addAll(0, vrtBrowseUseCase.liveStreams())
                        })
                )
                add(
                    ListRow(
                        HeaderItem(ctx.getString(R.string.vrt_nu_most_recent_episodes)),
                        ArrayObjectAdapter(PresenterSelector()).apply {
                            addAll(0, vrtBrowseUseCase.fetchMostRecentEpisodes())
                        })
                )
                add(
                    ListRow(
                        HeaderItem(ctx.getString(R.string.vrt_nu_all_programs)),
                        ArrayObjectAdapter(PresenterSelector()).apply {
                            addAll(0, vrtBrowseUseCase.fetchAZPrograms())
                        })
                )
                add(
                    ListRow(
                        HeaderItem(ctx.getString(R.string.vrt_nu_categories)),
                        ArrayObjectAdapter(PresenterSelector()).apply {
                            addAll(0, vrtBrowseUseCase.fetchCategories())
                        })
                )
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
            //TODO map to a detail object
            val clickedItem = when (val item = untypedItem as Item) {
                is Item.ImageCard.Live -> item.liveStream
                is Item.ImageCard.Category -> item.category
                is Item.ImageCard.Episode -> item.episode
                is Item.ImageCard.Program -> item.program
            }
            Log.d("TAG", "You clicked $clickedItem")
        }
    }
}
