package be.tapped.vlaamsetv.browse.vrt

import android.widget.ImageView
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vrtnu.content.VRTApi

interface VRTNUAZUseCase {

    suspend fun fetchAZPrograms(): List<Item>
}

class VRTNUAZUseCaseImpl(private val vrtApi: VRTApi) : VRTNUAZUseCase {

    override suspend fun fetchAZPrograms(): List<Item> =
        (vrtApi.fetchAZPrograms().orNull()?.programs ?: emptyList())
            .mapIndexed { index, program ->
                val background = if (program.alternativeImage.isNotBlank()) program.alternativeImage else program.thumbnail
                Item.ImageCard(
                    index = index,
                    title = program.title,
                    description = program.description,
                    thumbnail = program.thumbnail,
                    scaleType = ImageView.ScaleType.CENTER_CROP,
                    background = background
                )
            }
}
