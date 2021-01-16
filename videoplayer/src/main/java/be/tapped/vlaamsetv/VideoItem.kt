package be.tapped.vlaamsetv

import android.os.Parcelable
import com.google.android.exoplayer2.C
import kotlinx.parcelize.Parcelize

@Parcelize
public data class VideoItem(
    val url: String,
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
    public class Subtitle(public val subtitleUrl: String, public val mimeType: String, public val language: String? = null) :
        Parcelable

    @Parcelize
    public class Drm(public val licenseUrl: String, public val type: DrmType) : Parcelable {

        public enum class DrmType { WIDEVINE,
            PLAYREADY
        }
    }

}
