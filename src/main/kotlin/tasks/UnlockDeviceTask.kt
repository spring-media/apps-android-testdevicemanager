package tasks

import com.android.ddmlib.IDevice
import internal.DeviceCommunicator
import internal.DeviceWrapper
import internal.ShellCommands.INPUT_PRESS_POWER_BUTTON
import internal.ShellCommands.INPUT_WAKE_UP_CALL
import internal.TaskInfo.GROUP_NAME
import internal.devicesCanBeFound
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import tasks.internal.Unlocker

open class UnlockDeviceTask : DefaultTask() {

    init {
        group = GROUP_NAME
        description = "unlock the device"
    }

    companion object {
        const val MINIMUM_DIGITS = 4
    }

    private lateinit var device: IDevice
    private lateinit var deviceWrapper: DeviceWrapper

    @Input
    lateinit var unlockBy: String

    @Input
    lateinit var pin: String

    @Input
    lateinit var password: String

    @Input
    lateinit var communicator: DeviceCommunicator

    @TaskAction
    fun unlock() {
        val bridge = communicator.bridge
        val provider = communicator.outputReceiverProvider

        bridge.devicesCanBeFound()

        bridge.devices.forEach { device ->
            deviceWrapper = DeviceWrapper(device, provider)
            this.device = device

            activateDisplay()

            val unlocker = Unlocker(deviceWrapper, unlockBy, pin, password)
            unlocker.unlock()

            println("Screen of device ${deviceWrapper.getDetails()} activated & unlocked.")
        }
    }

    private fun activateDisplay() {
        if (!device.version.isGreaterOrEqualThan(20)) {
            if (!deviceWrapper.isDisplayOn()) {
                deviceWrapper.executeShellCommandWithOutput(INPUT_PRESS_POWER_BUTTON)
            }
        } else {
            deviceWrapper.executeShellCommandWithOutput(INPUT_WAKE_UP_CALL)
        }
    }
}
