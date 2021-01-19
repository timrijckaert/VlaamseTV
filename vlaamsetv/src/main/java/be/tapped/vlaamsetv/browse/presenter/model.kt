package be.tapped.vlaamsetv.browse.presenter

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

sealed class Item {

    abstract val index: Int

    data class ImageCard(
        override val index: Int,
        val title: String? = null,
        val description: String? = null,
        @DrawableRes val infoAreaBackground: Int? = null,
        val infoAreaBackgroundColor: Int? = null,
        val imageViewUrl: String? = null,
        @DrawableRes val badgeImage: Int? = null,
    ) : Item()
}
