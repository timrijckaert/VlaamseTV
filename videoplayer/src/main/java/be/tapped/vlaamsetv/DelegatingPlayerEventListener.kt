package be.tapped.vlaamsetv

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import kotlinx.coroutines.channels.SendChannel

internal class DelegatingPlayerEventListener(private val sendChannel: SendChannel<VideoEvent>) : Player.EventListener {

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        sendChannel.safeOffer(VideoEvent.Player.TimelineChanged(timeline, reason))
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        sendChannel.safeOffer(VideoEvent.Player.MediaItemTransition(mediaItem, reason))
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
        sendChannel.safeOffer(VideoEvent.Player.TracksChanged(trackGroups, trackSelections))
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        sendChannel.safeOffer(VideoEvent.Player.IsLoadingChanged(isLoading))
    }

    override fun onPlaybackStateChanged(state: Int) {
        sendChannel.safeOffer(VideoEvent.Player.PlaybackStateChanged(state))
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        sendChannel.safeOffer(VideoEvent.Player.PlayWhenReadyChanged(playWhenReady, reason))
    }

    override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
        sendChannel.safeOffer(VideoEvent.Player.PlaybackSuppressionReasonChanged(playbackSuppressionReason))
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        sendChannel.safeOffer(VideoEvent.Player.IsPlayingChanged(isPlaying))
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        sendChannel.safeOffer(VideoEvent.Player.RepeatModeChanged(repeatMode))
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        sendChannel.safeOffer(VideoEvent.Player.ShuffleModeEnabledChanged(shuffleModeEnabled))
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        sendChannel.safeOffer(VideoEvent.Player.PlayerError(error))
    }

    override fun onPositionDiscontinuity(reason: Int) {
        sendChannel.safeOffer(VideoEvent.Player.PositionDiscontinuity(reason))
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
        sendChannel.safeOffer(VideoEvent.Player.PlaybackParametersChanged(playbackParameters))
    }

    override fun onExperimentalOffloadSchedulingEnabledChanged(offloadSchedulingEnabled: Boolean) {
        sendChannel.safeOffer(VideoEvent.Player.ExperimentalOffloadSchedulingEnabledChanged(offloadSchedulingEnabled))
    }
}
