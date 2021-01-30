package be.tapped.vlaamsetv.detail

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import be.tapped.vlaamsetv.AppState
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vlaamsetv.browse.presenter.PresenterSelector
import be.tapped.vlaamsetv.browse.presenter.TypedPresenter
import be.tapped.vrtnu.content.LiveStreams
import coil.imageLoader
import coil.request.ImageRequest

class DetailFragment(private val appStateController: AppState.Controller) : DetailsSupportFragment(), OnItemViewClickedListener {

    private lateinit var mRowsAdapter: ArrayObjectAdapter
    private val mDetailsBackground = DetailsSupportFragmentBackgroundController(this)
    private val selectedItem get() = appStateController.currentState as AppState.Detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Terzake"
        val rowPresenter =
            object : FullWidthDetailsOverviewRowPresenter(
                object : TypedPresenter<TextView, Any>() {
                    override fun onCreateViewHolder(parent: ViewGroup, context: Context): TextView = TextView(parent.context)

                    override fun bindViewHolder(viewHolder: ViewHolder, view: TextView, item: Any) {
                        view.text = "$item"
                    }
                }
            ) {}

        rowPresenter.isParticipatingEntranceTransition = false
        prepareEntranceTransition()

        val rowPresenterSelector = ClassPresenterSelector()
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, rowPresenter)
        rowPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        mRowsAdapter = ArrayObjectAdapter(rowPresenterSelector)

        // Setup action and detail row.
        val detailsOverview =
            DetailsOverviewRow("Van maandag tot en met vrijdag brengt Terzake duiding bij het nieuws van de dag. Voor de kijker die beter wil begrijpen, kaderen Annelies Beck en Kathleen Cools de actualiteit aan de hand van kritische en verhelderende interviews.")

        requireContext().imageLoader.enqueue(
            ImageRequest.Builder(requireContext())
                .data("https://images.vrt.be/orig/2020/09/01/939e2c56-ec5a-11ea-aae0-02b7b76bf47f.jpg")
                .target { drawable -> detailsOverview.imageDrawable = drawable }
                .build()
        )

        requireContext().imageLoader.enqueue(
            ImageRequest.Builder(requireContext())
                .data("https://images.vrt.be/orig/2020/08/31/71a43a3c-eba9-11ea-aae0-02b7b76bf47f.jpg")
                .target { drawable -> mDetailsBackground.coverBitmap = (drawable as BitmapDrawable).bitmap }
                .build()
        )

        // Actions
        val actionAdapter = ArrayObjectAdapter()
        val play = Action(
            0L,
            "Seizoen 1",
        )
        actionAdapter.add(play)

        detailsOverview.actionsAdapter = actionAdapter
        mRowsAdapter.add(detailsOverview)

        // Setup related row.
        mRowsAdapter.add(
            ListRow(
                HeaderItem("Seizoen 1"),
                ArrayObjectAdapter(PresenterSelector()).apply {
                    addAll(0, LiveStreams.allLiveStreams.map {
                        Item.ImageCard.Live.VRT(
                            liveStream = it,
                            brandName = it.name,
                            brandImageUrl = null,
                            image = null,
                        )
                    })
                })
        )

        adapter = mRowsAdapter
        startEntranceTransition()

        mDetailsBackground.enableParallax()
        onItemViewClickedListener = this
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder,
        item: Any,
        rowViewHolder: RowPresenter.ViewHolder,
        row: Row
    ) {
        if (item !is Action) return
        setSelectedPosition(item.id.toInt() + 1)
    }
}
