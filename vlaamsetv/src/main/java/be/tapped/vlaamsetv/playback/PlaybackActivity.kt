package be.tapped.vlaamsetv.playback

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import be.tapped.vlaamsetv.PlayerManager
import be.tapped.vlaamsetv.R
import be.tapped.vlaamsetv.VideoPlayerTVFragment
import kotlinx.coroutines.launch

class PlaybackActivity : FragmentActivity(R.layout.activity_tv_playback) {

    private val navArgs by navArgs<PlaybackActivityArgs>()

    private val navHostFragment
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

    private val playerManager: PlayerManager = PlayerManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = object : FragmentFactory() {
            override fun instantiate(cls: ClassLoader, className: String): Fragment =
                when (className) {
                    VideoPlayerTVFragment::class.java.name -> VideoPlayerTVFragment(navArgs.videoItem, playerManager)
                    else                                   -> super.instantiate(cls, className)
                }
        }
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            playerManager.videoEvents.collect {
                Log.d("TAG", "$it")
            }
        }
    }

    //TODO PIP
    // override fun onUserLeaveHint() {
    //     enterPictureInPictureMode(PictureInPictureParams.Builder().build())
    // }
}
