package be.tapped.vlaamsetv

import com.google.android.exoplayer2.video.VideoListener
import kotlinx.coroutines.channels.SendChannel

internal class DelegatingVideoListener(private val sendChannel: SendChannel<VideoEvent>) : VideoListener {

    override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
        sendChannel.safeOffer(VideoEvent.Video.SizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio))
    }

    override fun onSurfaceSizeChanged(width: Int, height: Int) {
        sendChannel.safeOffer(VideoEvent.Video.SurfaceSizeChanged(width, height))
    }

    override fun onRenderedFirstFrame() {
        sendChannel.safeOffer(VideoEvent.Video.RenderedFirstFrame)
    }
}
