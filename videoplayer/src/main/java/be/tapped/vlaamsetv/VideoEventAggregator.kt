package be.tapped.vlaamsetv

import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.EventLogger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal val SimpleExoPlayer.eventFlow: Flow<VideoEvent>
    get() =
        with(this) {
            callbackFlow {
                val eventLogger = EventLogger(null)
                val delegatingAnalyticsListener = DelegatingAnalyticsListener(channel)
                val delegatingPlayerEventListener = DelegatingPlayerEventListener(channel)
                val delegatingMetadataOutputListener = DelegatingMetadataOutputListener(channel)
                val delegatingVideoListener = DelegatingVideoListener(channel)
                val delegatingAudioListener = DelegatingAudioListener(channel)
                val delegatingDeviceListener = DelegatingDeviceListener(channel)

                addAnalyticsListener(eventLogger)
                addAnalyticsListener(delegatingAnalyticsListener)
                addListener(delegatingPlayerEventListener)
                addMetadataOutput(delegatingMetadataOutputListener)
                addVideoListener(delegatingVideoListener)
                addAudioListener(delegatingAudioListener)
                addDeviceListener(delegatingDeviceListener)

                awaitClose {
                    removeAnalyticsListener(eventLogger)
                    removeAnalyticsListener(delegatingAnalyticsListener)
                    removeListener(delegatingPlayerEventListener)
                    removeMetadataOutput(delegatingMetadataOutputListener)
                    removeVideoListener(delegatingVideoListener)
                    removeAudioListener(delegatingAudioListener)
                    removeDeviceListener(delegatingDeviceListener)
                }
            }
        }
