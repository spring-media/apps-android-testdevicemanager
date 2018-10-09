package internal

import com.android.ddmlib.AndroidDebugBridge

data class DeviceCommunicator(
        val bridge: AndroidDebugBridge,
        val outputReceiverProvider: OutputReceiverProvider
)

