package be.tapped.vlaamsetv.browse.presenter

import android.widget.ImageView
import androidx.annotation.DrawableRes

sealed class Item {

    abstract val index: Int

    data class ImageCard(
        override val index: Int,
        val title: String? = null,
        val description: String? = null,
        @DrawableRes val infoAreaBackground: Int? = null,
        val infoAreaBackgroundColor: Int? = null,
        val background: String? = null,
        val thumbnail: String? = null,
        @DrawableRes val badgeImageRes: Int? = null,
        val badgeImageUrl: String? = null,
        val scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER
    ) : Item()
}
