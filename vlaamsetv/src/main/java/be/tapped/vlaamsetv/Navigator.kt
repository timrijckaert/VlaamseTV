package be.tapped.vlaamsetv

import androidx.navigation.NavController
import be.tapped.vlaamsetv.auth.AuthenticationNavigation
import be.tapped.vlaamsetv.auth.AuthenticationNavigationConfiguration
import be.tapped.vlaamsetv.auth.prefs.TokenStorage
import be.tapped.vlaamsetv.browse.BrowseNavigation
import be.tapped.vlaamsetv.playback.PlaybackNavigation

interface RootNavigator {

    suspend fun moveToStartDestination()

    companion object {

        internal fun create(navigator: Navigator, tokenStorage: TokenStorage): RootNavigator = object : RootNavigator {
            override suspend fun moveToStartDestination() {
                val hasCredentialsForAtLeastOneBrand = tokenStorage.hasCredentialsForAtLeastOneBrand()
                if (hasCredentialsForAtLeastOneBrand) {
                    navigator.navigateToBrowseContent()
                    // Test Leanback playback
                    // navigator.navigateToPlayback(
                    //     VideoItem(
                    //         url = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd",
                    //         title = "This is a test video",
                    //         subtitle = "This is a subtitle that can span multiple lines of text. And so it will probably wrap and at some point maybe even ellipsize.",
                    //         art = "https://images.vrt.be/orig/2021/01/07/d53ce59e-50e6-11eb-aae0-02b7b76bf47f.jpg"
                    //     )
                    // )
                } else {
                    navigator.navigateToAuthenticationFlow(
                        arrayOf(
                            AuthenticationNavigationConfiguration.VRT,
                            AuthenticationNavigationConfiguration.VTM,
                            AuthenticationNavigationConfiguration.GoPlay,
                        )
                    )
                }
            }
        }
    }
}

class Navigator(private val navController: NavController) : AuthenticationNavigation,
                                                            PlaybackNavigation,
                                                            BrowseNavigation {

    override fun navigateToAuthenticationFlow(config: Array<AuthenticationNavigationConfiguration>) {
        navController.navigate(MainFragmentDirections.actionMainFragmentToAuthenticationFlowTv(config))
    }

    override fun navigateToPlayback(videoItem: VideoItem) {
        navController.navigate(MainFragmentDirections.actionMainFragmentToPlaybackActivity(videoItem))
    }

    override fun navigateToBrowseContent() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToBrowseActivity())
    }
}
