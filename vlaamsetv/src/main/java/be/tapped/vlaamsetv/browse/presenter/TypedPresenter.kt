package be.tapped.vlaamsetv.browse.presenter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter

abstract class TypedPresenter<T : View, in U> : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(onCreateViewHolder(parent, parent.context))

    abstract fun onCreateViewHolder(parent: ViewGroup, context: Context): T

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        bindViewHolder(viewHolder, viewHolder.view as T, item as U)
    }

    abstract fun bindViewHolder(viewHolder: ViewHolder, view: T, item: U)

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        onUnbindViewHolder(viewHolder, viewHolder.view as T)
    }

    fun onUnbindViewHolder(viewHolder: ViewHolder, view: T) {
        // Can optionally be overridden to release resources
    }
}
