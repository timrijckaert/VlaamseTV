package be.tapped.vlaamsetv

import android.os.Parcelable
import com.google.android.exoplayer2.C
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoItem(
    val url: String,
    val title: String,
    val subtitle: String,
    val art: String? = null,
    val drm: Drm? = null,
    val subtitles: List<Subtitle> = emptyList(),
    val startAutoPlay: Boolean = DEFAULT_START_AUTO_PLAY,
    val startWindow: Int = DEFAULT_START_WINDOW,
    val startPosition: Long = DEFAULT_START_POSITION,
    val showDebug: Boolean = DEFAULT_SHOW_DEBUG,
) : Parcelable {

    internal companion object {

        internal const val DEFAULT_START_AUTO_PLAY = true
        internal const val DEFAULT_SHOW_DEBUG = false
        internal const val DEFAULT_START_WINDOW = C.INDEX_UNSET
        internal const val DEFAULT_START_POSITION = C.INDEX_UNSET.toLong()
    }

    @Parcelize
    class Subtitle(val subtitleUrl: String, val mimeType: String, val language: String? = null) :
        Parcelable

    @Parcelize
    class Drm(val licenseUrl: String, val type: DrmType) : Parcelable {

        enum class DrmType {
            WIDEVINE,
            PLAYREADY
        }
    }

}
