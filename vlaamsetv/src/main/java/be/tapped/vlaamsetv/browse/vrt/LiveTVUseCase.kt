package be.tapped.vlaamsetv.browse.vrt

import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vrtnu.content.DefaultScreenshotRepo
import be.tapped.vrtnu.content.LiveStreams
import be.tapped.vrtnu.content.LiveStreams.LiveStream.Brand
import be.tapped.vrtnu.content.ScreenshotRepo

interface LiveTVUseCase {

    suspend fun liveStreams(): List<Item>
}

class LiveTVUseCaseImpl : LiveTVUseCase {

    //TODO check if it has an EPG and fetch it
    override suspend fun liveStreams(): List<Item> =
        LiveStreams.allLiveStreams.mapIndexed { index, it ->
            val badgeImageUrl = when (it.brand) {
                Brand.EEN,
                Brand.CANVAS,
                Brand.KETNET -> null
                Brand.KETNET_JUNIOR -> "https://images.vrt.be/orig/2019/07/19/c309360a-aa10-11e9-abcc-02b7b76bf47f.png"
                Brand.SPORZA -> "https://images.vrt.be/orig/logo/sporza/sporza_logo_zwart.png"
                Brand.VRT_NWS -> "https://images.vrt.be/orig/logos/vrtnws.png"
                Brand.RADIO_1 -> "https://images.vrt.be/orig/logos/radio1.png"
                Brand.RADIO_2 -> "https://images.vrt.be/orig/logos/radio2.png"
                Brand.KLARA -> "https://images.vrt.be/orig/logos/klara.png"
                Brand.STUDIO_BRUSSEL -> "https://images.vrt.be/orig/2019/03/12/1e383cf5-44a7-11e9-abcc-02b7b76bf47f.png"
                Brand.MNM -> "https://images.vrt.be/orig/logo/mnm/logo_witte_achtergrond.png"
                Brand.VRT_NXT -> "https://images.vrt.be/orig/logo/vrt.png"
            }
            val background = backgroundFromBrand(it.brand)
            Item.ImageCard.Live(
                liveStream = it,
                brandName = it.name,
                brandImageUrl = badgeImageUrl,
                image = background,
            )
        }

    private fun backgroundFromBrand(brand: Brand): String? =
        when (brand) {
            Brand.EEN -> DefaultScreenshotRepo.screenshotForBrand(ScreenshotRepo.Brand.EEN)
            Brand.CANVAS -> DefaultScreenshotRepo.screenshotForBrand(ScreenshotRepo.Brand.CANVAS)
            Brand.KETNET -> DefaultScreenshotRepo.screenshotForBrand(ScreenshotRepo.Brand.KETNET)
            else -> null
        }
}
