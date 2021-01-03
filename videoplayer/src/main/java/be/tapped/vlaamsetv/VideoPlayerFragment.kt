package be.tapped.vlaamsetv

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.PlayerManager._videoEvents
import be.tapped.vlaamsetv.VideoItem.Companion.DEFAULT_START_WINDOW
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.DebugTextViewHelper
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.math.max

@Suppress("unused")
public class VideoPlayerFragment : Fragment(R.layout.fragment_video) {

    private lateinit var playerView: StyledPlayerView
    private lateinit var debugTextView: TextView

    private var player: SimpleExoPlayer? = null
    private var debugViewHelper: DebugTextViewHelper? = null

    private lateinit var videoItem: VideoItem

    public companion object {
        private const val VIDEO_ITEM_KEY = "be.tapped.vlaamsetv.VideoPlayerFragment.VIDEO_ITEM"

        public fun newInstance(videoItem: VideoItem): VideoPlayerFragment =
            VideoPlayerFragment().apply {
                arguments = bundleOf(VIDEO_ITEM_KEY to videoItem)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = checkNotNull(savedInstanceState ?: arguments)
        videoItem = checkNotNull(bundle.getParcelable(VIDEO_ITEM_KEY))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerView = view.findViewById(R.id.playerView)
        debugTextView = view.findViewById(R.id.debug_text_view)
        playerView.requestFocus()
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
        playerView.onResume()
    }

    override fun onStop() {
        super.onStop()
        playerView.onPause()
        releasePlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        updateStartPosition()
        outState.putParcelable(VIDEO_ITEM_KEY, videoItem)
    }

    private fun initializePlayer() {
        if (player == null) {
            PlayerManager.exoPlayer(requireContext()).let { p ->
                @Suppress("ConvertReferenceToLambda")
                lifecycleScope.launch {
                    p.eventFlow.flowOn(Dispatchers.Default).collect(_videoEvents::emit)
                }
                p.playWhenReady = videoItem.startAutoPlay
                playerView.player = p

                if (videoItem.showDebug) {
                    debugTextView.isVisible = videoItem.showDebug
                    debugViewHelper = DebugTextViewHelper(p, debugTextView)
                    debugViewHelper?.start()
                }

                player = p
            }
        }

        val haveStartPosition = videoItem.startWindow != DEFAULT_START_WINDOW
        if (haveStartPosition) {
            player?.seekTo(videoItem.startWindow, videoItem.startPosition)
        }

        val mediaItem = MediaItem.Builder()
            .setUri(videoItem.url)
            .apply {
                videoItem.drm?.let {
                    setDrmUuid(
                        when (it.type) {
                            VideoItem.Drm.DrmType.WIDEVINE -> C.WIDEVINE_UUID
                            VideoItem.Drm.DrmType.PLAYREADY -> C.PLAYREADY_UUID
                        }
                    )
                    setDrmLicenseUri(it.licenseUrl)
                }

                setSubtitles(
                    videoItem.subtitles.map {
                        MediaItem.Subtitle(
                            Uri.parse(it.subtitleUrl),
                            it.mimeType,
                            it.language
                        )
                    }
                )
            }
            .build()
        player?.setMediaItem(mediaItem, !haveStartPosition)
        player?.prepare()
    }

    private fun updateStartPosition() {
        player?.let {
            videoItem = videoItem.copy(
                startAutoPlay = it.playWhenReady,
                startWindow = it.currentWindowIndex,
                startPosition = max(0, it.contentPosition),
            )
        }
    }

    private fun releasePlayer() {
        updateStartPosition()
        debugViewHelper?.stop()
        debugViewHelper = null
        player?.release()
        player = null
    }
}
