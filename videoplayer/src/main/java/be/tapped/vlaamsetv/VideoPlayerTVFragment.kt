package be.tapped.vlaamsetv

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class VideoPlayerTVFragment(
    private val videoItem: VideoItem,
    private val playerManager: PlayerManager,
) : VideoSupportFragment() {

    private lateinit var player: SimpleExoPlayer
    private lateinit var playerGlue: MediaPlayerGlue

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private inner class MediaPlayerGlue(context: Context, adapter: LeanbackPlayerAdapter) :
        PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, adapter) {

        private val actionRewind = PlaybackControlsRow.RewindAction(context)
        private val actionFastForward = PlaybackControlsRow.FastForwardAction(context)
        private val actionClosedCaptions = PlaybackControlsRow.ClosedCaptioningAction(context)

        fun skipForward(millis: Long = SKIP_PLAYBACK_MILLIS) =
            player.seekTo(if (player.contentDuration > 0) {
                min(player.contentDuration, player.currentPosition + millis)
            } else {
                player.currentPosition + millis
            })

        fun skipBackward(millis: Long = SKIP_PLAYBACK_MILLIS) =
            player.seekTo(max(0, player.currentPosition - millis))

        override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter) {
            super.onCreatePrimaryActions(adapter)
            adapter.add(actionRewind)
            adapter.add(actionFastForward)
            adapter.add(actionClosedCaptions)
        }

        override fun onActionClicked(action: Action) = when (action) {
            actionRewind -> skipBackward()
            actionFastForward -> skipForward()
            else              -> super.onActionClicked(action)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePlayer()

        val playerAdapter = LeanbackPlayerAdapter(requireContext(), player, PLAYER_UPDATE_INTERVAL_MILLIS)

        playerGlue = MediaPlayerGlue(requireContext(), playerAdapter).apply {
            host = VideoSupportFragmentGlueHost(this@VideoPlayerTVFragment)
        }

        mediaSession = MediaSessionCompat(requireContext(), "rtugrtugrth")
        mediaSessionConnector = MediaSessionConnector(mediaSession)

        adapter = ArrayObjectAdapter(playerGlue.playbackRowPresenter).apply {
            add(playerGlue.controlsRow)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.BLACK)
    }

    private fun initializePlayer() {
        if (!::player.isInitialized) {
            playerManager.exoPlayer(requireContext()).let { p ->
                @Suppress("ConvertReferenceToLambda") lifecycleScope.launch {
                    p.eventFlow.flowOn(Dispatchers.Default).collect(playerManager._videoEvents::emit)
                }
                p.playWhenReady = videoItem.startAutoPlay
                player = p
            }
        }

        val haveStartPosition = videoItem.startWindow != VideoItem.DEFAULT_START_WINDOW
        if (haveStartPosition) {
            player.seekTo(videoItem.startWindow, videoItem.startPosition)
        }

        val mediaItem = MediaItem.Builder().setUri(videoItem.url).apply {
            videoItem.drm?.let {
                setDrmUuid(when (it.type) {
                               VideoItem.Drm.DrmType.WIDEVINE  -> C.WIDEVINE_UUID
                               VideoItem.Drm.DrmType.PLAYREADY -> C.PLAYREADY_UUID
                           })
                setDrmLicenseUri(it.licenseUrl)
            }

            setSubtitles(videoItem.subtitles.map {
                MediaItem.Subtitle(Uri.parse(it.subtitleUrl), it.mimeType, it.language)
            })
        }.build()
        player.setMediaItem(mediaItem, !haveStartPosition)
        player.prepare()
    }

    override fun onResume() {
        super.onResume()
        mediaSessionConnector.setPlayer(player)
        mediaSession.isActive = true
    }

    override fun onStop() {
        super.onStop()

        mediaSession.isActive = false
        mediaSessionConnector.setPlayer(null)
        player.release()
        mediaSession.release()
    }

    companion object {

        private const val PLAYER_UPDATE_INTERVAL_MILLIS: Int = 100
        private val SKIP_PLAYBACK_MILLIS: Long = TimeUnit.SECONDS.toMillis(30)
    }

}
