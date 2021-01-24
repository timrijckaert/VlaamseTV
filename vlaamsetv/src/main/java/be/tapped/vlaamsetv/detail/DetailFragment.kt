package be.tapped.vlaamsetv.detail

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.browse.presenter.TypedPresenter

class DetailFragment : DetailsSupportFragment() {

    private lateinit var mRowsAdapter: ArrayObjectAdapter
    private val mDetailsBackground = DetailsSupportFragmentBackgroundController(this)

    data class SampleObject(val sampleField: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rowPresenter =
            object : FullWidthDetailsOverviewRowPresenter(
                object : TypedPresenter<TextView, Any>() {
                    override fun onCreateViewHolder(parent: ViewGroup, context: Context): TextView = TextView(parent.context)

                    override fun bindViewHolder(viewHolder: ViewHolder, view: TextView, item: Any) {
                        view.text = "Hello World"
                    }
                }
            ) {}

        rowPresenter.isParticipatingEntranceTransition = false
        prepareEntranceTransition()

        val rowPresenterSelector = ClassPresenterSelector()
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, rowPresenter)
        mRowsAdapter = ArrayObjectAdapter(rowPresenterSelector)

        // Setup action and detail row.
        val detailsOverview = DetailsOverviewRow("hello world")
        // Set small thumbnail image
        // detailsOverview.imageDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.background_canyon)
        detailsOverview.item = SampleObject("helelreregher ")
        // detailsOverview.imageDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.vrt_nu_logo)

        // Actions
        val actionAdapter = ArrayObjectAdapter()
        val play = Action(
            0L,
            "Label 1",
        )
        val action2 = Action(
            1L,
            "Label 2",
            "Label 2"
        )
        val action3 = Action(
            2L,
            "Label 3",
            "Label 3",
            ContextCompat.getDrawable(requireContext(), R.drawable.vrt_nu_logo)
        )
        val action4 = Action(
            3L,
            "Label 3",
            null,
            ContextCompat.getDrawable(requireContext(), R.drawable.vrt_nu_logo)
        )

        actionAdapter.add(play)
        actionAdapter.add(action2)
        actionAdapter.add(action3)
        actionAdapter.add(action4)

        detailsOverview.actionsAdapter = actionAdapter
        mRowsAdapter.add(detailsOverview)

        adapter = mRowsAdapter
        startEntranceTransition()

        mDetailsBackground.enableParallax()
        // set background
        //mDetailsBackground.coverBitmap = BitmapFactory.decodeResource(
        //    resources,
        //    R.drawable.background_canyon
        //)
    }
}
