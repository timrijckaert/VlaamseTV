package be.tapped.vlaamsetv.browse.vrt

import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vrtnu.content.VRTApi

interface VRTNUAZUseCase {

    suspend fun fetchAZPrograms(): List<Item>
}

class VRTNUAZUseCaseImpl(
    private val vrtApi: VRTApi,
    private val programMapper: ProgramMapper,
) : VRTNUAZUseCase {

    override suspend fun fetchAZPrograms(): List<Item> =
        (vrtApi.fetchAZPrograms().orNull()?.programs ?: emptyList())
            .mapIndexed(programMapper::toImageCard)
}
