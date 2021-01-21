package be.tapped.vlaamsetv.browse.presenter

import android.content.Context
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import be.tapped.vlaamsetv.R
import coil.imageLoader
import coil.load
import coil.request.ImageRequest

class ImageCardViewPresenter : TypedPresenter<ImageCardView, Item.ImageCard>() {

    override fun onCreateViewHolder(parent: ViewGroup, context: Context): ImageCardView = ImageCardView(parent.context)

    override fun bindViewHolder(viewHolder: ViewHolder, cardView: ImageCardView, item: Item.ImageCard) {
        with(cardView) {
            titleText = item.title
            contentText = item.description
            setMainImageDimensions(
                resources.getDimensionPixelSize(R.dimen.card_width),
                resources.getDimensionPixelSize(R.dimen.card_height),
            )
            infoAreaBackground = item.infoAreaBackground?.let { ContextCompat.getDrawable(context, it) }
            item.infoAreaBackgroundColor?.let(::setInfoAreaBackgroundColor)
            setMainImageScaleType(item.scaleType)
            badgeImage = item.badgeImageRes?.let { ContextCompat.getDrawable(cardView.context, it) }
            context.imageLoader.enqueue(
                ImageRequest.Builder(context)
                    .data(item.badgeImageUrl)
                    .target { drawable -> badgeImage = drawable }
                    .build()
            )
            mainImageView.load(item.thumbnail)
        }
    }
}

