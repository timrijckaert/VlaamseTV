package be.tapped.vlaamsetv.sample

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import be.tapped.vlaamsetv.VideoItem
import be.tapped.vlaamsetv.VideoPlayerFragment
import be.tapped.vlaamsetv.sample.databinding.ActivityMainBinding

public data class SampleVideoItem(val title: String, val videoItem: VideoItem) {
    override fun toString(): String = title
}

public class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val testVideoSpinner get() = binding.testVideoSpinner

    private val sampleVideos = listOf(
        SampleVideoItem(
            "UHD DASH - Widevine",
            VideoItem(
                "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears_uhd.mpd",
                VideoItem.Drm(
                    "https://proxy.uat.widevine.com/proxy?provider=widevine_test",
                    VideoItem.Drm.DrmType.WIDEVINE,
                ),
            )
        ),
        SampleVideoItem(
            "Clear UHD",
            VideoItem("https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd")
        ),
        SampleVideoItem(
            "4K (MP4, H264, Widevine cenc)",
            VideoItem(
                "https://storage.googleapis.com/exoplayer-test-media-1/60fps/bbb-wv-2160/manifest.mpd",
                VideoItem.Drm(
                    "https://proxy.uat.widevine.com/proxy?provider=widevine_test",
                    VideoItem.Drm.DrmType.WIDEVINE,
                )
            )
        ),
        SampleVideoItem(
            "Subtitles",
            VideoItem(
                "https://html5demos.com/assets/dizzy.mp4",
                subtitles = listOf(
                    VideoItem.Subtitle(
                        "https://storage.googleapis.com/exoplayer-test-media-1/webvtt/numeric-lines.vtt",
                        "text/vtt",
                    )
                )
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        testVideoSpinner.onItemSelectedListener = this

        ArrayAdapter(this, android.R.layout.simple_spinner_item, sampleVideos).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            testVideoSpinner.adapter = adapter
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        supportFragmentManager.commit {
            replace(
                R.id.fragment_container_view,
                VideoPlayerFragment.newInstance(sampleVideos[position].videoItem),
            )
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}
