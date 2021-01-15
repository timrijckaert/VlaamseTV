package be.tapped.vlaamsetv

import android.content.Context
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

public object PlayerManager {

    @Suppress("ObjectPropertyName")
    internal val _videoEvents: MutableStateFlow<VideoEvent> by lazy { MutableStateFlow(VideoEvent.Unknown) }
    public val videoEvents: Flow<VideoEvent> get() = _videoEvents

    @Suppress("ConvertLambdaToReference")
    internal fun exoPlayer(context: Context): SimpleExoPlayer =
            SimpleExoPlayer.Builder(context).build()
}
