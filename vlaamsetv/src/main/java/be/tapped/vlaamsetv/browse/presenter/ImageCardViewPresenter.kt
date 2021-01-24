package be.tapped.vlaamsetv.browse.presenter

import android.content.Context
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import be.tapped.vlaamsetv.R
import coil.imageLoader
import coil.load
import coil.request.ImageRequest

abstract class ImageCardViewPresenter<in T : Item.ImageCard> : TypedPresenter<ImageCardView, T>() {

    override fun onCreateViewHolder(parent: ViewGroup, context: Context): ImageCardView = ImageCardView(parent.context)

    override fun bindViewHolder(viewHolder: ViewHolder, view: ImageCardView, item: T) {
        with(view) {
            titleText = item.title
            contentText = item.description
            setMainImageDimensions(
                resources.getDimensionPixelSize(R.dimen.card_width),
                resources.getDimensionPixelSize(R.dimen.card_height),
            )
            infoAreaBackground = item.infoAreaBackground?.let { ContextCompat.getDrawable(context, it) }
            item.infoAreaBackgroundColor?.let(::setInfoAreaBackgroundColor)
            setMainImageScaleType(item.scaleType)
            badgeImage = item.badgeImageRes?.let { ContextCompat.getDrawable(view.context, it) }
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

class LiveCardPresenter : ImageCardViewPresenter<Item.ImageCard.Live>()

class CategoryCardPresenter : ImageCardViewPresenter<Item.ImageCard.Category>()

class EpisodeCardPresenter : ImageCardViewPresenter<Item.ImageCard.Episode>()

class ProgramCardPresenter : ImageCardViewPresenter<Item.ImageCard.Program>()
