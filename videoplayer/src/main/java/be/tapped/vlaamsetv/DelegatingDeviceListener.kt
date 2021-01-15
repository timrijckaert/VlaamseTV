package be.tapped.vlaamsetv

import com.google.android.exoplayer2.device.DeviceInfo
import com.google.android.exoplayer2.device.DeviceListener
import kotlinx.coroutines.channels.SendChannel

internal class DelegatingDeviceListener(private val sendChannel: SendChannel<VideoEvent>) :
    DeviceListener {
    override fun onDeviceInfoChanged(deviceInfo: DeviceInfo) {
        sendChannel.safeOffer(VideoEvent.Device.InfoChanged(deviceInfo))
    }

    override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
        sendChannel.safeOffer(VideoEvent.Device.VolumeChanged(volume, muted))
    }
}
