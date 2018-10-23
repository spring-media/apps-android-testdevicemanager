package internal

import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import org.gradle.api.GradleException


fun AndroidDebugBridge.devicesCanBeFound() {
    if (this.devices.isEmpty()) {
        throw NoDevicesConnectedException()
    }
}

fun AppExtension.createAndroidDebugBridge(): AndroidDebugBridge {
    AndroidDebugBridge.initIfNeeded(false)
    return AndroidDebugBridge.createBridge(this.adbExecutable.path, false)
            ?: throw GradleException("ADB could not be created.")
}