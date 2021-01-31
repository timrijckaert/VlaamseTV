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
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vlaamsetv.browse.presenter.PresenterSelector
import be.tapped.vlaamsetv.browse.presenter.TypedPresenter
import coil.imageLoader
import coil.request.ImageRequest

class DetailFragment(private val input: Input) : DetailsSupportFragment(), OnItemViewClickedListener {

    data class Input(
        val title: String,
        val description: String,
        val posterImage: String,
        val coverImage: String,
        val seasons: List<Season>,
    ) {

        data class Season(val name: String, val items: List<Item>)
    }

    private val detailsBackground = DetailsSupportFragmentBackgroundController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = input.title

        // TODO we could extend this to a custom view more visually attractive design
        val infoPresenter =
            object : FullWidthDetailsOverviewRowPresenter(
                object : TypedPresenter<TextView, Any>() {
                    override fun onCreateViewHolder(parent: ViewGroup, context: Context): TextView = TextView(parent.context)

                    override fun bindViewHolder(viewHolder: ViewHolder, view: TextView, item: Any) {
                        view.text = "$item"
                    }
                }
            ) {}

        infoPresenter.isParticipatingEntranceTransition = false
        prepareEntranceTransition()

        val detailsOverview = DetailsOverviewRow(input.description)
            .apply {
                actionsAdapter = ArrayObjectAdapter().apply {
                    input.seasons.forEachIndexed { index, season ->
                        add(Action(index.toLong(), season.name))
                    }
                }
            }

        requireContext().imageLoader.enqueue(
            ImageRequest.Builder(requireContext())
                .data(input.posterImage)
                .target { drawable -> detailsOverview.imageDrawable = drawable }
                .build()
        )

        adapter = ArrayObjectAdapter(ClassPresenterSelector().apply {
            addClassPresenter(DetailsOverviewRow::class.java, infoPresenter)
            addClassPresenter(ListRow::class.java, ListRowPresenter())
        }).apply {
            add(detailsOverview)
            input.seasons.forEach { season ->
                add(
                    ListRow(
                        HeaderItem(season.name),
                        ArrayObjectAdapter(PresenterSelector()).apply {
                            addAll(0, season.items.map { it })
                        }
                    )
                )
            }
        }

        requireContext().imageLoader.enqueue(
            ImageRequest.Builder(requireContext())
                .data(input.coverImage)
                .target { drawable -> detailsBackground.coverBitmap = (drawable as BitmapDrawable).bitmap }
                .build()
        )

        startEntranceTransition()

        detailsBackground.enableParallax()
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
