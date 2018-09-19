package tasks

import ShellCommands.INPUT_PRESS_POWER_BUTTON
import ShellCommands.INPUT_SLEEP_CALL
import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import devicesCanBeFound
import executeShellCommandWithOutput
import getSdkVersion
import isDisplayOn
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction


open class LockDeviceTask : DefaultTask() {

    init {
        group = "device setup"
        description = "lock the device"
    }

    @Input
    lateinit var android: AppExtension

    @Input
    lateinit var bridge: AndroidDebugBridge

    @TaskAction
    fun lock() {
        bridge.devicesCanBeFound()

        bridge.devices.forEach {
            deactivateDisplay(it)
        }
    }

    private fun deactivateDisplay(device: IDevice) {
        val sdkVersion = device.getSdkVersion()

        if (sdkVersion < 20) {
            if (device.isDisplayOn()) {
                println("deactivating Display by power button")
                device.executeShellCommandWithOutput(INPUT_PRESS_POWER_BUTTON)
            }
        } else {
            println("deactivating Display by sleep call")
            device.executeShellCommandWithOutput(INPUT_SLEEP_CALL)
        }
    }
}