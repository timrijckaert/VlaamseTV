package be.tapped.vlaamsetv

import android.view.Surface
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.device.DeviceInfo
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import java.io.IOException

public sealed class VideoEvent {
    public object Unknown : VideoEvent()

    public sealed class Device : VideoEvent() {
        public data class InfoChanged(val deviceInfo: DeviceInfo) : Device()
        public data class VolumeChanged(val volume: Int, val muted: Boolean) : Device()
    }

    public sealed class Audio : VideoEvent() {
        public data class SessionId(val audioSessionId: Int) : Audio()
        public data class AttributesChanged(val audioAttributes: AudioAttributes) : Audio()
        public data class VolumeChanged(val volume: Float) : Audio()
        public data class SkipSilenceEnabledChanged(val skipSilenceEnabled: Boolean) : Audio()
    }

    public sealed class Video : VideoEvent() {
        public data class SizeChanged(
            val width: Int,
            val height: Int,
            val unappliedRotationDegrees: Int,
            val pixelWidthHeightRatio: Float
        ) : Video()

        public data class SurfaceSizeChanged(val width: Int, val height: Int) : Video()

        public object RenderedFirstFrame : Video()
    }

    public data class Metadata(val metadata: com.google.android.exoplayer2.metadata.Metadata) :
        VideoEvent()

    public sealed class Player : VideoEvent() {
        public data class TimelineChanged(val timeline: Timeline, val reason: Int) : Player()
        public data class MediaItemTransition(val mediaItem: MediaItem?, val reason: Int) : Player()
        public data class TracksChanged(
            val trackGroups: TrackGroupArray,
            val trackSelections: TrackSelectionArray
        ) : Player()

        public data class IsLoadingChanged(val isLoading: Boolean) : Player()
        public data class PlaybackStateChanged(val state: Int) : Player()
        public data class PlayWhenReadyChanged(val playWhenReady: Boolean, val reason: Int) :
            Player()

        public data class PlaybackSuppressionReasonChanged(val playbackSuppressionReason: Int) :
            Player()

        public data class IsPlayingChanged(val isPlaying: Boolean) : Player()
        public data class RepeatModeChanged(val repeatMode: Int) : Player()
        public data class ShuffleModeEnabledChanged(val shuffleModeEnabled: Boolean) : Player()
        public data class PlayerError(val error: ExoPlaybackException) : Player()
        public data class PositionDiscontinuity(val reason: Int) : Player()
        public data class PlaybackParametersChanged(val playbackParameters: PlaybackParameters) :
            Player()

        public data class ExperimentalOffloadSchedulingEnabledChanged(val offloadSchedulingEnabled: Boolean) :
            Player()
    }

    public sealed class Analytics : VideoEvent() {
        public data class PlaybackStateChanged(
            val eventTime: AnalyticsListener.EventTime,
            val state: Int
        ) : Analytics()

        public data class PlayWhenReadyChanged(
            val eventTime: AnalyticsListener.EventTime,
            val playWhenReady: Boolean,
            val reason: Int
        ) : Analytics()

        public data class PlaybackSuppressionReasonChanged(
            val eventTime: AnalyticsListener.EventTime,
            val playbackSuppressionReason: Int
        ) : Analytics()

        public data class IsPlayingChanged(
            val eventTime: AnalyticsListener.EventTime,
            val isPlaying: Boolean
        ) : Analytics()

        public data class TimelineChanged(
            val eventTime: AnalyticsListener.EventTime,
            val reason: Int
        ) :
            Analytics()

        public data class MediaItemTransition(
            val eventTime: AnalyticsListener.EventTime,
            val mediaItem: MediaItem?,
            val reason: Int
        ) : Analytics()

        public data class PositionDiscontinuity(
            val eventTime: AnalyticsListener.EventTime,
            val reason: Int
        ) : Analytics()

        public data class SeekStarted(val eventTime: AnalyticsListener.EventTime) : Analytics()
        public data class PlaybackParametersChanged(
            val eventTime: AnalyticsListener.EventTime,
            val playbackParameters: PlaybackParameters
        ) : Analytics()

        public data class RepeatModeChanged(
            val eventTime: AnalyticsListener.EventTime,
            val repeatMode: Int
        ) : Analytics()

        public data class ShuffleModeChanged(
            val eventTime: AnalyticsListener.EventTime,
            val shuffleModeEnabled: Boolean
        ) : Analytics()

        public data class IsLoadingChanged(
            val eventTime: AnalyticsListener.EventTime,
            val isLoading: Boolean
        ) : Analytics()

        public data class PlayerError(
            val eventTime: AnalyticsListener.EventTime,
            val error: ExoPlaybackException
        ) : Analytics()

        public data class TracksChanged(
            val eventTime: AnalyticsListener.EventTime,
            val trackGroups: TrackGroupArray,
            val trackSelections: TrackSelectionArray
        ) : Analytics()

        public data class LoadStarted(
            val eventTime: AnalyticsListener.EventTime,
            val loadEventInfo: LoadEventInfo,
            val mediaLoadData: MediaLoadData
        ) : Analytics()

        public data class LoadCompleted(
            val eventTime: AnalyticsListener.EventTime,
            val loadEventInfo: LoadEventInfo,
            val mediaLoadData: MediaLoadData
        ) : Analytics()

        public data class LoadCanceled(
            val eventTime: AnalyticsListener.EventTime,
            val loadEventInfo: LoadEventInfo,
            val mediaLoadData: MediaLoadData
        ) : Analytics()

        public data class LoadError(
            val eventTime: AnalyticsListener.EventTime,
            val loadEventInfo: LoadEventInfo,
            val mediaLoadData: MediaLoadData,
            val error: IOException,
            val wasCanceled: Boolean
        ) : Analytics()

        public data class DownstreamFormatChanged(
            val eventTime: AnalyticsListener.EventTime,
            val mediaLoadData: MediaLoadData
        ) : Analytics()

        public data class UpstreamDiscarded(
            val eventTime: AnalyticsListener.EventTime,
            val mediaLoadData: MediaLoadData
        ) : Analytics()

        public data class BandwidthEstimate(
            val eventTime: AnalyticsListener.EventTime,
            val totalLoadTimeMs: Int,
            val totalBytesLoaded: Long,
            val bitrateEstimate: Long
        ) : Analytics()

        public data class Metadata(
            val eventTime: AnalyticsListener.EventTime,
            val metadata: com.google.android.exoplayer2.metadata.Metadata
        ) : Analytics()

        public data class AudioEnabled(
            val eventTime: AnalyticsListener.EventTime,
            val counters: DecoderCounters
        ) : Analytics()

        public data class AudioDecoderInitialized(
            val eventTime: AnalyticsListener.EventTime,
            val decoderName: String,
            val initializationDurationMs: Long
        ) : Analytics()

        public data class AudioInputFormatChanged(
            val eventTime: AnalyticsListener.EventTime,
            val format: Format
        ) : Analytics()

        public data class AudioPositionAdvancing(
            val eventTime: AnalyticsListener.EventTime,
            val playoutStartSystemTimeMs: Long
        ) : Analytics()

        public data class AudioUnderrun(
            val eventTime: AnalyticsListener.EventTime,
            val bufferSize: Int,
            val bufferSizeMs: Long,
            val elapsedSinceLastFeedMs: Long
        ) : Analytics()

        public data class AudioDisabled(
            val eventTime: AnalyticsListener.EventTime,
            val counters: DecoderCounters
        ) : Analytics()

        public data class AudioSessionId(
            val eventTime: AnalyticsListener.EventTime,
            val audioSessionId: Int
        ) : Analytics()

        public data class AudioAttributesChanged(
            val eventTime: AnalyticsListener.EventTime,
            val audioAttributes: AudioAttributes
        ) : Analytics()

        public data class SkipSilenceEnabledChanged(
            val eventTime: AnalyticsListener.EventTime,
            val skipSilenceEnabled: Boolean
        ) : Analytics()

        public data class VolumeChanged(
            val eventTime: AnalyticsListener.EventTime,
            val volume: Float
        ) :
            Analytics()

        public data class VideoEnabled(
            val eventTime: AnalyticsListener.EventTime,
            val counters: DecoderCounters
        ) : Analytics()

        public data class VideoDecoderInitialized(
            val eventTime: AnalyticsListener.EventTime,
            val decoderName: String,
            val initializationDurationMs: Long
        ) : Analytics()

        public data class VideoInputFormatChanged(
            val eventTime: AnalyticsListener.EventTime,
            val format: Format
        ) : Analytics()

        public data class DroppedVideoFrames(
            val eventTime: AnalyticsListener.EventTime,
            val droppedFrames: Int,
            val elapsedMs: Long
        ) : Analytics()

        public data class VideoDisabled(
            val eventTime: AnalyticsListener.EventTime,
            val counters: DecoderCounters
        ) : Analytics()

        public data class VideoFrameProcessingOffset(
            val eventTime: AnalyticsListener.EventTime,
            val totalProcessingOffsetUs: Long,
            val frameCount: Int
        ) : Analytics()

        public data class RenderedFirstFrame(
            val eventTime: AnalyticsListener.EventTime,
            val surface: Surface?
        ) : Analytics()

        public data class VideoSizeChanged(
            val eventTime: AnalyticsListener.EventTime,
            val width: Int,
            val height: Int,
            val unappliedRotationDegrees: Int,
            val pixelWidthHeightRatio: Float
        ) : Analytics()

        public data class SurfaceSizeChanged(
            val eventTime: AnalyticsListener.EventTime,
            val width: Int,
            val height: Int
        ) : Analytics()

        public data class DrmSessionAcquired(val eventTime: AnalyticsListener.EventTime) :
            Analytics()

        public data class DrmKeysLoaded(val eventTime: AnalyticsListener.EventTime) : Analytics()
        public data class DrmSessionManagerError(
            val eventTime: AnalyticsListener.EventTime,
            val error: Exception
        ) : Analytics()

        public data class DrmKeysRestored(val eventTime: AnalyticsListener.EventTime) : Analytics()
        public data class DrmKeysRemoved(val eventTime: AnalyticsListener.EventTime) : Analytics()
        public data class DrmSessionReleased(val eventTime: AnalyticsListener.EventTime) :
            Analytics()
    }
}
