package be.tapped.vlaamsetv.browse.vrt

import android.graphics.Color
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vrtnu.content.DefaultScreenshotRepo
import be.tapped.vrtnu.content.LiveStreams
import be.tapped.vrtnu.content.LiveStreams.LiveStream.Brand
import be.tapped.vrtnu.content.ScreenshotRepo

interface LiveTVUseCase {

    suspend fun liveStreams(): List<Item>
}

class LiveTVUseCaseImpl : LiveTVUseCase {

    override suspend fun liveStreams(): List<Item> =
        LiveStreams.allLiveStreams.mapIndexed { index, it ->
            val screenGrab = when (it.brand) {
                Brand.EEN -> DefaultScreenshotRepo.screenshotForBrand(ScreenshotRepo.Brand.EEN)
                Brand.CANVAS -> DefaultScreenshotRepo.screenshotForBrand(ScreenshotRepo.Brand.CANVAS)
                Brand.KETNET -> DefaultScreenshotRepo.screenshotForBrand(ScreenshotRepo.Brand.KETNET)
                Brand.KETNET_JUNIOR,
                Brand.SPORZA,
                Brand.VRT_NWS,
                Brand.RADIO_1,
                Brand.RADIO_2,
                Brand.KLARA,
                Brand.STUDIO_BRUSSEL,
                Brand.MNM,
                Brand.VRT_NXT -> null
            }
            Item.ImageCard(
                index = index,
                title = it.name,
                imageViewUrl = screenGrab,
                infoAreaBackgroundColor = if (screenGrab.isNullOrBlank()) Color.RED else null
            )
        }

}
