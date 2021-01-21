package be.tapped.vlaamsetv.browse.vrt

import android.widget.ImageView
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vrtnu.content.VRTApi
import kotlinx.coroutines.flow.first

interface MostRecentUseCase {

    suspend fun fetchMostRecentEpisodes(): List<Item>
}

class MostRecentUseCaseImpl(private val vrtApi: VRTApi) : MostRecentUseCase {

    override suspend fun fetchMostRecentEpisodes(): List<Item> {
        val episodes = vrtApi.fetchMostRecent().first().orNull()?.episodes ?: emptyList()
        return episodes.mapIndexed { index, episode ->
            Item.ImageCard(
                index,
                title = episode.program,
                description = episode.subtitle,
                scaleType = ImageView.ScaleType.CENTER_CROP,
                thumbnail = episode.videoThumbnailUrl,
                background = episode.programImageUrl
            )
        }
    }

}
