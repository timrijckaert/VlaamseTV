package be.tapped.vlaamsetv

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.MimeTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@Suppress("unused")
public class VideoPlayerFragment : Fragment(R.layout.fragment_video) {

    private lateinit var playerView: StyledPlayerView
    private val exoPlayer: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(requireContext()).build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerView = view.findViewById(R.id.playerView)
        playerView.player = exoPlayer

        lifecycleScope.launch {
            exoPlayer.eventFlow.flowOn(Dispatchers.Default)
                .collect {
                    Log.d("${this::class.simpleName}", "$it")
                }
        }

        val mediaItem = MediaItem.Builder()
            .setUri(
                ""
            )
            .setDrmUuid(C.WIDEVINE_UUID)
            .setDrmLicenseUri("")
            .setSubtitles(
                listOf(
                    MediaItem.Subtitle(
                        Uri.parse(""),
                        MimeTypes.TEXT_VTT,
                        null
                    )
                )
            )
            //.setDrmLicenseRequestHeaders(httpRequestHeaders)
            .setDrmMultiSession(true)
            .build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playerView.player = null
    }
}
