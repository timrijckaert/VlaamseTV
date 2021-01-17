package be.tapped.vlaamsetv.playback

import be.tapped.vlaamsetv.VideoItem

interface PlaybackNavigation {

    fun navigateToPlayback(videoItem: VideoItem)
}
