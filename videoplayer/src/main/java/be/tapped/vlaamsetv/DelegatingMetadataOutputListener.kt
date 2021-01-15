package be.tapped.vlaamsetv

import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.MetadataOutput
import kotlinx.coroutines.channels.SendChannel

internal class DelegatingMetadataOutputListener(private val sendChannel: SendChannel<VideoEvent>) :
        MetadataOutput {
    override fun onMetadata(metadata: Metadata) {
        sendChannel.safeOffer(VideoEvent.Metadata(metadata))
    }
}
