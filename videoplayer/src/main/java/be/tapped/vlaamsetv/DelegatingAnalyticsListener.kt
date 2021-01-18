package be.tapped.vlaamsetv

import android.view.Surface
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import kotlinx.coroutines.channels.SendChannel
import java.io.IOException

internal class DelegatingAnalyticsListener(private val sendChannel: SendChannel<VideoEvent>) : AnalyticsListener {

    override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
        sendChannel.safeOffer(VideoEvent.Analytics.PlaybackStateChanged(eventTime, state))
    }

    override fun onPlayWhenReadyChanged(eventTime: AnalyticsListener.EventTime, playWhenReady: Boolean, reason: Int) {
        sendChannel.safeOffer(VideoEvent.Analytics.PlayWhenReadyChanged(eventTime, playWhenReady, reason))
    }

    override fun onPlaybackSuppressionReasonChanged(eventTime: AnalyticsListener.EventTime, playbackSuppressionReason: Int) {
        sendChannel.safeOffer(VideoEvent.Analytics.PlaybackSuppressionReasonChanged(eventTime, playbackSuppressionReason))
    }

    override fun onIsPlayingChanged(eventTime: AnalyticsListener.EventTime, isPlaying: Boolean) {
        sendChannel.safeOffer(VideoEvent.Analytics.IsPlayingChanged(eventTime, isPlaying))
    }

    override fun onTimelineChanged(eventTime: AnalyticsListener.EventTime, reason: Int) {
        sendChannel.safeOffer(VideoEvent.Analytics.TimelineChanged(eventTime, reason))
    }

    override fun onMediaItemTransition(eventTime: AnalyticsListener.EventTime, mediaItem: MediaItem?, reason: Int) {
        sendChannel.safeOffer(VideoEvent.Analytics.MediaItemTransition(eventTime, mediaItem, reason))
    }

    override fun onPositionDiscontinuity(eventTime: AnalyticsListener.EventTime, reason: Int) {
        sendChannel.safeOffer(VideoEvent.Analytics.PositionDiscontinuity(eventTime, reason))
    }

    override fun onSeekStarted(eventTime: AnalyticsListener.EventTime) {
        sendChannel.safeOffer(VideoEvent.Analytics.SeekStarted(eventTime))
    }

    override fun onPlaybackParametersChanged(eventTime: AnalyticsListener.EventTime, playbackParameters: PlaybackParameters) {
        sendChannel.safeOffer(VideoEvent.Analytics.PlaybackParametersChanged(eventTime, playbackParameters))
    }

    override fun onRepeatModeChanged(eventTime: AnalyticsListener.EventTime, repeatMode: Int) {
        sendChannel.safeOffer(VideoEvent.Analytics.RepeatModeChanged(eventTime, repeatMode))
    }

    override fun onShuffleModeChanged(eventTime: AnalyticsListener.EventTime, shuffleModeEnabled: Boolean) {
        sendChannel.safeOffer(VideoEvent.Analytics.ShuffleModeChanged(eventTime, shuffleModeEnabled))
    }

    override fun onIsLoadingChanged(eventTime: AnalyticsListener.EventTime, isLoading: Boolean) {
        sendChannel.safeOffer(VideoEvent.Analytics.IsLoadingChanged(eventTime, isLoading))
    }

    override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: ExoPlaybackException) {
        sendChannel.safeOffer(VideoEvent.Analytics.PlayerError(eventTime, error))
    }

    override fun onTracksChanged(
        eventTime: AnalyticsListener.EventTime,
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray,
    ) {
        sendChannel.safeOffer(VideoEvent.Analytics.TracksChanged(eventTime, trackGroups, trackSelections))
    }

    override fun onLoadStarted(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData,
    ) {
        sendChannel.safeOffer(VideoEvent.Analytics.LoadStarted(eventTime, loadEventInfo, mediaLoadData))
    }

    override fun onLoadCompleted(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData,
    ) {
        sendChannel.safeOffer(VideoEvent.Analytics.LoadCompleted(eventTime, loadEventInfo, mediaLoadData))
    }

    override fun onLoadCanceled(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData,
    ) {
        sendChannel.safeOffer(VideoEvent.Analytics.LoadCanceled(eventTime, loadEventInfo, mediaLoadData))
    }

    override fun onLoadError(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData,
        error: IOException,
        wasCanceled: Boolean,
    ) {
        sendChannel.safeOffer(VideoEvent.Analytics.LoadError(eventTime, loadEventInfo, mediaLoadData, error, wasCanceled))
    }

    override fun onDownstreamFormatChanged(eventTime: AnalyticsListener.EventTime, mediaLoadData: MediaLoadData) {
        sendChannel.safeOffer(VideoEvent.Analytics.DownstreamFormatChanged(eventTime, mediaLoadData))
    }

    override fun onUpstreamDiscarded(eventTime: AnalyticsListener.EventTime, mediaLoadData: MediaLoadData) {
        sendChannel.safeOffer(VideoEvent.Analytics.UpstreamDiscarded(eventTime, mediaLoadData))
    }

    override fun onBandwidthEstimate(
        eventTime: AnalyticsListener.EventTime,
        totalLoadTimeMs: Int,
        totalBytesLoaded: Long,
        bitrateEstimate: Long,
    ) {
        sendChannel.safeOffer(VideoEvent.Analytics.BandwidthEstimate(eventTime,
            totalLoadTimeMs,
            totalBytesLoaded,
            bitrateEstimate))
    }

    override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
        sendChannel.safeOffer(VideoEvent.Analytics.Metadata(eventTime, metadata))
    }

    override fun onAudioEnabled(eventTime: AnalyticsListener.EventTime, counters: DecoderCounters) {
        sendChannel.safeOffer(VideoEvent.Analytics.AudioEnabled(eventTime, counters))
    }

    override fun onAudioDecoderInitialized(
        eventTime: AnalyticsListener.EventTime,
        decoderName: String,
        initializationDurationMs: Long,
    ) {
        sendChannel.safeOffer(VideoEvent.Analytics.AudioDecoderInitialized(eventTime, decoderName, initializationDurationMs))
    }

    override fun onAudioInputFormatChanged(eventTime: AnalyticsListener.EventTime, format: Format) {
        sendChannel.safeOffer(VideoEvent.Analytics.AudioInputFormatChanged(eventTime, format))
    }

    override fun onAudioPositionAdvancing(eventTime: AnalyticsListener.EventTime, playoutStartSystemTimeMs: Long) {
        sendChannel.safeOffer(VideoEvent.Analytics.AudioPositionAdvancing(eventTime, playoutStartSystemTimeMs))
    }

    override fun onAudioUnderrun(
        eventTime: AnalyticsListener.EventTime,
        bufferSize: Int,
        bufferSizeMs: Long,
        elapsedSinceLastFeedMs: Long,
    ) {
        sendChannel.safeOffer(VideoEvent.Analytics.AudioUnderrun(eventTime, bufferSize, bufferSizeMs, elapsedSinceLastFeedMs))
    }

    override fun onAudioDisabled(eventTime: AnalyticsListener.EventTime, counters: DecoderCounters) {
        sendChannel.safeOffer(VideoEvent.Analytics.AudioDisabled(eventTime, counters))
    }

    override fun onAudioSessionId(eventTime: AnalyticsListener.EventTime, audioSessionId: Int) {
        sendChannel.safeOffer(VideoEvent.Analytics.AudioSessionId(eventTime, audioSessionId))
    }

    override fun onAudioAttributesChanged(eventTime: AnalyticsListener.EventTime, audioAttributes: AudioAttributes) {
        sendChannel.safeOffer(VideoEvent.Analytics.AudioAttributesChanged(eventTime, audioAttributes))
    }

    override fun onSkipSilenceEnabledChanged(eventTime: AnalyticsListener.EventTime, skipSilenceEnabled: Boolean) {
        sendChannel.safeOffer(VideoEvent.Analytics.SkipSilenceEnabledChanged(eventTime, skipSilenceEnabled))
    }

    override fun onVolumeChanged(eventTime: AnalyticsListener.EventTime, volume: Float) {
        sendChannel.safeOffer(VideoEvent.Analytics.VolumeChanged(eventTime, volume))
    }

    override fun onVideoEnabled(eventTime: AnalyticsListener.EventTime, counters: DecoderCounters) {
        sendChannel.safeOffer(VideoEvent.Analytics.VideoEnabled(eventTime, counters))
    }

    override fun onVideoDecoderInitialized(
        eventTime: AnalyticsListener.EventTime,
        decoderName: String,
        initializationDurationMs: Long,
    ) {
        sendChannel.safeOffer(VideoEvent.Analytics.VideoDecoderInitialized(eventTime, decoderName, initializationDurationMs))
    }

    override fun onVideoInputFormatChanged(eventTime: AnalyticsListener.EventTime, format: Format) {
        sendChannel.safeOffer(VideoEvent.Analytics.VideoInputFormatChanged(eventTime, format))
    }

    override fun onDroppedVideoFrames(eventTime: AnalyticsListener.EventTime, droppedFrames: Int, elapsedMs: Long) {
        sendChannel.safeOffer(VideoEvent.Analytics.DroppedVideoFrames(eventTime, droppedFrames, elapsedMs))
    }

    override fun onVideoDisabled(eventTime: AnalyticsListener.EventTime, counters: DecoderCounters) {
        sendChannel.safeOffer(VideoEvent.Analytics.VideoDisabled(eventTime, counters))
    }

    override fun onVideoFrameProcessingOffset(
        eventTime: AnalyticsListener.EventTime,
        totalProcessingOffsetUs: Long,
        frameCount: Int,
    ) {
        sendChannel.safeOffer(VideoEvent.Analytics.VideoFrameProcessingOffset(eventTime, totalProcessingOffsetUs, frameCount))
    }

    override fun onRenderedFirstFrame(eventTime: AnalyticsListener.EventTime, surface: Surface?) {
        sendChannel.safeOffer(VideoEvent.Analytics.RenderedFirstFrame(eventTime, surface))
    }

    override fun onVideoSizeChanged(
        eventTime: AnalyticsListener.EventTime,
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float,
    ) {
        sendChannel.safeOffer(VideoEvent.Analytics.VideoSizeChanged(eventTime,
            width,
            height,
            unappliedRotationDegrees,
            pixelWidthHeightRatio))
    }

    override fun onSurfaceSizeChanged(eventTime: AnalyticsListener.EventTime, width: Int, height: Int) {
        sendChannel.safeOffer(VideoEvent.Analytics.SurfaceSizeChanged(eventTime, width, height))
    }

    override fun onDrmSessionAcquired(eventTime: AnalyticsListener.EventTime) {
        sendChannel.safeOffer(VideoEvent.Analytics.DrmSessionAcquired(eventTime))
    }

    override fun onDrmKeysLoaded(eventTime: AnalyticsListener.EventTime) {
        sendChannel.safeOffer(VideoEvent.Analytics.DrmKeysLoaded(eventTime))
    }

    override fun onDrmSessionManagerError(eventTime: AnalyticsListener.EventTime, error: Exception) {
        sendChannel.safeOffer(VideoEvent.Analytics.DrmSessionManagerError(eventTime, error))
    }

    override fun onDrmKeysRestored(eventTime: AnalyticsListener.EventTime) {
        sendChannel.safeOffer(VideoEvent.Analytics.DrmKeysRestored(eventTime))
    }

    override fun onDrmKeysRemoved(eventTime: AnalyticsListener.EventTime) {
        sendChannel.safeOffer(VideoEvent.Analytics.DrmKeysRemoved(eventTime))
    }

    override fun onDrmSessionReleased(eventTime: AnalyticsListener.EventTime) {
        sendChannel.safeOffer(VideoEvent.Analytics.DrmSessionReleased(eventTime))
    }
}
