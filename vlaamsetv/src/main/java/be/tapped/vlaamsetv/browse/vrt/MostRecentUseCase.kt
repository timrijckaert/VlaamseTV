package be.tapped.vlaamsetv.browse.vrt

import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vrtnu.content.VRTApi
import kotlinx.coroutines.flow.first

interface MostRecentUseCase {

    suspend fun fetchMostRecentEpisodes(): List<Item>
}

class MostRecentUseCaseImpl(private val vrtApi: VRTApi) : MostRecentUseCase {

    override suspend fun fetchMostRecentEpisodes(): List<Item> {
        val episodes = vrtApi.fetchMostRecent().first().orNull()?.episodes ?: emptyList()
        return episodes.map { episode ->
            Item.ImageCard.Episode(
                episode,
                episode.program,
                episode.subtitle,
                episode.videoThumbnailUrl,
                episode.programImageUrl
            )
        }
    }

}
