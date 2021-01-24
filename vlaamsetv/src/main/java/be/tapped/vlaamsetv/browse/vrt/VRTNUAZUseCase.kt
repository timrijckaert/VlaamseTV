package be.tapped.vlaamsetv.browse.vrt

import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vrtnu.content.VRTApi

interface VRTNUAZUseCase {

    suspend fun fetchAZPrograms(): List<Item>
}

class VRTNUAZUseCaseImpl(private val vrtApi: VRTApi) : VRTNUAZUseCase {

    override suspend fun fetchAZPrograms(): List<Item> =
        (vrtApi.fetchAZPrograms().orNull()?.programs ?: emptyList())
            .map { program ->
                val background = if (program.alternativeImage.isNotBlank()) program.alternativeImage else program.thumbnail
                Item.ImageCard.Program(
                    program,
                    program.title,
                    program.description,
                    program.thumbnail,
                    background
                )
            }
}
