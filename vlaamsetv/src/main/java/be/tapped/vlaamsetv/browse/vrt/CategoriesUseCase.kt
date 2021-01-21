package be.tapped.vlaamsetv.browse.vrt

import android.widget.ImageView
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vrtnu.content.VRTApi

interface CategoriesUseCase {

    suspend fun fetchCategories(): List<Item.ImageCard>
}

class CategoriesUseCaseImpl(private val vrtApi: VRTApi) : CategoriesUseCase {

    override suspend fun fetchCategories(): List<Item.ImageCard> {
        val categories = (vrtApi.fetchCategories().orNull()?.categories ?: emptyList())
        return categories.mapIndexed { index, category ->
            Item.ImageCard(
                index,
                category.title,
                category.description,
                thumbnail = category.imageStoreUrl,
                scaleType = ImageView.ScaleType.CENTER_CROP,
                background = category.imageStoreUrl
            )
        }
    }

}
