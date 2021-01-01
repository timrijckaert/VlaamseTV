package be.tapped.vlaamsetv

import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.audio.AudioListener
import kotlinx.coroutines.channels.SendChannel

internal class DelegatingAudioListener(private val sendChannel: SendChannel<VideoEvent>) : AudioListener {
    override fun onAudioSessionId(audioSessionId: Int) {
        sendChannel.safeOffer(VideoEvent.Audio.SessionId(audioSessionId))
    }

    override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {
        sendChannel.safeOffer(VideoEvent.Audio.AttributesChanged(audioAttributes))
    }

    override fun onVolumeChanged(volume: Float) {
        sendChannel.safeOffer(VideoEvent.Audio.VolumeChanged(volume))
    }

    override fun onSkipSilenceEnabledChanged(skipSilenceEnabled: Boolean) {
        sendChannel.safeOffer(VideoEvent.Audio.SkipSilenceEnabledChanged(skipSilenceEnabled))
    }
}
