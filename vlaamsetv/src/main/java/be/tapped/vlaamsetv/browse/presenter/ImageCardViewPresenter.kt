package be.tapped.vlaamsetv.browse.presenter

import android.content.Context
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import be.tapped.vlaamsetv.R
import coil.load

class ImageCardViewPresenter : TypedPresenter<ImageCardView, Item.ImageCard>() {

    override fun onCreateViewHolder(parent: ViewGroup, context: Context): ImageCardView = ImageCardView(parent.context)

    override fun bindViewHolder(viewHolder: ViewHolder, view: ImageCardView, item: Item.ImageCard) {
        with(view) {
            titleText = item.title
            contentText = item.description
            setMainImageDimensions(
                resources.getDimensionPixelSize(R.dimen.card_width),
                resources.getDimensionPixelSize(R.dimen.card_height),
            )
            mainImageView.load(item.imageViewUrl)
        }
    }
}

