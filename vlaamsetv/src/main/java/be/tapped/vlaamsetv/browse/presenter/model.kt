package be.tapped.vlaamsetv.browse.presenter

import android.widget.ImageView
import androidx.annotation.DrawableRes
import be.tapped.vrtnu.content.LiveStreams

sealed class Item {

    sealed class ImageCard(
        val title: String? = null,
        val description: String? = null,
        @DrawableRes val infoAreaBackground: Int? = null,
        val infoAreaBackgroundColor: Int? = null,
        val background: String? = null,
        val thumbnail: String? = null,
        @DrawableRes val badgeImageRes: Int? = null,
        val badgeImageUrl: String? = null,
        val scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER
    ) : Item() {

        sealed class Live(bName: String, brandImg: String?, img: String?) : ImageCard(
            title = bName,
            badgeImageUrl = brandImg,
            background = img,
            thumbnail = img
        ) {

            data class VRT(
                val liveStream: LiveStreams.LiveStream,
                val brandName: String,
                val brandImageUrl: String?,
                val image: String?,
            ) : Live(brandName, brandImageUrl, image)
        }

        data class Category(
            val category: be.tapped.vrtnu.content.Category,
            val categoryName: String,
            val categoryDescription: String?,
            val categoryImage: String,
            val categoryBackground: String
        ) : ImageCard(
            title = categoryName,
            description = categoryDescription,
            thumbnail = categoryImage,
            scaleType = ImageView.ScaleType.CENTER_CROP,
            background = categoryBackground
        )

        data class Program(
            val program: be.tapped.vrtnu.content.Program,
            val programName: String,
            val programDescription: String,
            val programThumbnail: String,
            val programBackground: String
        ) : ImageCard(
            title = programName,
            description = programDescription,
            thumbnail = programThumbnail,
            scaleType = ImageView.ScaleType.CENTER_CROP,
            background = programBackground
        )

        data class Episode(
            val episode: be.tapped.vrtnu.content.Episode,
            val programName: String,
            val subtitle: String?,
            val episodeThumbnail: String,
            val episodeBackground: String,
        ) : ImageCard(
            title = programName,
            description = subtitle,
            scaleType = ImageView.ScaleType.CENTER_CROP,
            thumbnail = episodeThumbnail,
            background = episodeBackground
        )
    }
}
