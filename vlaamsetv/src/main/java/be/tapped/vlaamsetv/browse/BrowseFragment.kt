package be.tapped.vlaamsetv.browse

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.PageRow
import androidx.leanback.widget.Presenter
import be.tapped.vlaamsetv.R

class SomeFragment : Fragment(R.layout.fragment_some), BrowseSupportFragment.MainFragmentAdapterProvider {

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return BrowseSupportFragment.MainFragmentAdapter(this)
    }
}

class BrowseFragment : BrowseSupportFragment() {

    private lateinit var mCategoryRowAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareEntranceTransition()
        mCategoryRowAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = mCategoryRowAdapter

        mainFragmentRegistry.registerFragment(PageRow::class.java,
            object : BrowseSupportFragment.FragmentFactory<Fragment>() {
                override fun createFragment(row: Any?): Fragment {
                    return SomeFragment()
                }
            })

        val headerItem = HeaderItem("Google+")
        val arrayObjectAdapter = ArrayObjectAdapter(object : Presenter() {
            override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
                val cardView: ImageCardView = object : ImageCardView(parent.context) {
                    override fun setSelected(selected: Boolean) {
                        updateCardBackgroundColor(this, selected)
                        super.setSelected(selected)
                    }
                }

                cardView.isFocusable = true
                cardView.isFocusableInTouchMode = true
                updateCardBackgroundColor(cardView, false)
                return ViewHolder(cardView)
            }

            private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
                val color: Int = if (selected) Color.RED else Color.GREEN

                view.setBackgroundColor(color)
                view.findViewById<View>(R.id.info_field).setBackgroundColor(color)
            }

            override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
                val cardView = viewHolder.view as ImageCardView
                cardView.titleText = "$item"
                cardView.contentText = "Content text"
            }

            override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

            }
        })
        arrayObjectAdapter.add("This")
        arrayObjectAdapter.add("is")
        arrayObjectAdapter.add("a")
        arrayObjectAdapter.add("test")
        val listRow = ListRow(headerItem, arrayObjectAdapter)
        mCategoryRowAdapter.add(PageRow(headerItem))
    }

}
