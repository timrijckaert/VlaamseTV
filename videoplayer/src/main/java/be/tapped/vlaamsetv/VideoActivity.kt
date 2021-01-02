package be.tapped.vlaamsetv

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import be.tapped.vlaamsetv.databinding.ActivityVideoBinding
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.MimeTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

public class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    private val videoView: StyledPlayerView get() = binding.playerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val simpleExoPlayer: SimpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        lifecycleScope.launch {
            simpleExoPlayer.eventFlow
                .flowOn(Dispatchers.IO)
                .collect {
                    Log.d("VideoPlayer", "$it")
                }
        }
        videoView.player = simpleExoPlayer

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
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
    }
}
