package tasks

import internal.DeviceCommunicator
import internal.DeviceWrapper
import internal.ShellCommands.INPUT_PRESS_POWER_BUTTON
import internal.ShellCommands.INPUT_SLEEP_CALL
import internal.TaskInfo.GROUP_NAME
import internal.devicesCanBeFound
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction


open class LockDeviceTask : DefaultTask() {

    init {
        group = GROUP_NAME
        description = "lock the device"
    }

    @Input
    lateinit var communicator: DeviceCommunicator

    @TaskAction
    fun lock() {
        val bridge = communicator.bridge
        val provider = communicator.outputReceiverProvider

        bridge.devicesCanBeFound()

        bridge.devices.forEach {
            val deviceWrapper = DeviceWrapper(it, provider)
            deactivateDisplay(deviceWrapper)
        }
    }

    private fun deactivateDisplay(deviceWrapper: DeviceWrapper) {

        val device = deviceWrapper.device

        if (!device.version.isGreaterOrEqualThan(20)) {
            if (deviceWrapper.isDisplayOn()) {
                deviceWrapper.executeShellCommandWithOutput(INPUT_PRESS_POWER_BUTTON)
            }
        } else {
            deviceWrapper.executeShellCommandWithOutput(INPUT_SLEEP_CALL)
        }

        println("Screen of device ${deviceWrapper.getDetails()} deactivated & locked.")
    }
}