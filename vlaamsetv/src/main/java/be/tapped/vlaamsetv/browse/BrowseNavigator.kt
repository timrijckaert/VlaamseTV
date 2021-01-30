package be.tapped.vlaamsetv.browse

import androidx.navigation.NavController
import be.tapped.vlaamsetv.VideoItem
import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vlaamsetv.detail.DetailNavigation
import be.tapped.vlaamsetv.playback.PlaybackNavigation

interface BrowseNavigator : DetailNavigation, PlaybackNavigation {
    companion object {

        fun create(navController: NavController): BrowseNavigator =
            object : BrowseNavigator {

                private fun clickedItemToBrowseNavigationAction(clickedItem: Item): Any {
                    return when (clickedItem) {
                        is Item.ImageCard.Live ->
                            when (clickedItem) {
                                //TODO go to playback screen
                                is Item.ImageCard.Live.VRT -> clickedItem.liveStream
                            }
                        is Item.ImageCard.Category -> clickedItem.category //TODO go to category detail screen
                        is Item.ImageCard.Episode -> clickedItem.episode //TODO go to playback screen
                        is Item.ImageCard.Program -> clickedItem.program
                    }
                }

                override fun navigateToDetail(clickedItem: Item) {
                    val detailItem = clickedItemToBrowseNavigationAction(clickedItem)

                    navController.navigate(BrowseFragmentDirections.actionBrowseFragmentToDetailActivity())
                }

                override fun navigateToPlayback(videoItem: VideoItem) {
                    // TODO live items and episodes should skip the detail screen and start playing upon click
                }
            }
    }
}

