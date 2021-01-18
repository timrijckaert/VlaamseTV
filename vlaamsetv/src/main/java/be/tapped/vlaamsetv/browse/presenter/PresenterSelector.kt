package be.tapped.vlaamsetv.browse.presenter

import androidx.leanback.widget.Presenter

class PresenterSelector : androidx.leanback.widget.PresenterSelector() {

    override fun getPresenter(item: Any): Presenter =
        when (item as Item) {
            is Item.ImageCard -> ImageCardViewPresenter()
        }
}
