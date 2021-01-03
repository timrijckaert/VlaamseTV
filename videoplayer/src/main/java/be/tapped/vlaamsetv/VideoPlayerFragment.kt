package be.tapped.vlaamsetv

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.PlayerManager._videoEvents
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.math.max

@Suppress("unused")
public class VideoPlayerFragment : Fragment(R.layout.fragment_video) {

    private lateinit var playerView: StyledPlayerView
    private lateinit var player: SimpleExoPlayer

    private var startAutoPlay = false
    private var startWindow = C.INDEX_UNSET
    private var startPosition: Long = C.INDEX_UNSET.toLong()

    private companion object {
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
        private const val KEY_AUTO_PLAY = "auto_play"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            startWindow = savedInstanceState.getInt(KEY_WINDOW)
            startPosition = savedInstanceState.getLong(KEY_POSITION)
        }
        playerView = view.findViewById(R.id.playerView)
        playerView.requestFocus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        updateStartPosition()
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay)
        outState.putInt(KEY_WINDOW, startWindow)
        outState.putLong(KEY_POSITION, startPosition)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
        playerView.onResume()
    }

    override fun onResume() {
        super.onResume()
        playerView.onResume()
    }

    override fun onStop() {
        super.onStop()
        playerView.onPause()
        releasePlayer()
    }

    private fun initializePlayer() {
        player = PlayerManager.exoPlayer(requireContext())
        @Suppress("ConvertReferenceToLambda")
        lifecycleScope.launch {
            player.eventFlow.flowOn(Dispatchers.Default)
                .collect(_videoEvents::emit)
        }
        player.playWhenReady = startAutoPlay
        playerView.player = player

        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player.seekTo(startWindow, startPosition)
        }

        val mediaItem = MediaItem.Builder()
            .setUri("https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears_uhd.mpd")
            .setDrmUuid(C.WIDEVINE_UUID)
            .setDrmLicenseUri("https://proxy.uat.widevine.com/proxy?provider=widevine_test")
            .setSubtitles(
                listOf(
                    MediaItem.Subtitle(
                        Uri.parse("https://dvt-subtitles.persgroep.be/TZITINDEF17_1F712940_12b5fdb3-b5bb-49c0-8e29-0a46b9525f0c_vtt_nl.vtt"),
                        "text/vtt",
                        null
                    )
                )
            )
            .build()
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    private fun updateStartPosition() {
        startAutoPlay = player.playWhenReady
        startWindow = player.currentWindowIndex
        startPosition = max(0, player.contentPosition)
    }

    private fun releasePlayer() {
        player.release()
    }
}
