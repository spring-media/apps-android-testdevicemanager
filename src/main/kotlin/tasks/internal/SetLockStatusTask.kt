package tasks.internal

import com.android.ddmlib.IDevice
import internal.DeviceWrapper
import internal.ShellCommands.INPUT_PRESS_POWER_BUTTON
import internal.ShellCommands.INPUT_SLEEP_CALL
import internal.ShellCommands.INPUT_WAKE_UP_CALL
import tasks.UnlockDeviceTask.Companion.ANDROID_API_LEVEL_44W

open class SetLockStatusTask(private val lockDevice: Boolean) : DefaultPluginTask() {

    private lateinit var deviceWrapper: DeviceWrapper

    override fun runTask1() {}

    override fun runTask2() {}

    override fun runTaskFor(device: IDevice) {
        deviceWrapper = DeviceWrapper(device, outputReceiverProvider)

        if (device.version.isGreaterOrEqualThan(ANDROID_API_LEVEL_44W))
            setDeviceStatusForNewerDevices()
        else
            setDeviceStatusForOlderDevices()
    }

    override fun runPostTask() {}

    private fun setDeviceStatusForNewerDevices() {
        val shellCommand = if (!lockDevice)
            INPUT_WAKE_UP_CALL
        else
            INPUT_SLEEP_CALL
        deviceWrapper.executeShellCommandWithOutput(shellCommand)
        outputDisplayStatus(true)
    }

    private fun setDeviceStatusForOlderDevices() {
        val requiresStatusChange = (!lockDevice && !deviceWrapper.isDisplayOn()) || (lockDevice && deviceWrapper.isDisplayOn())
        val requiresNoStatusChange = (!lockDevice && deviceWrapper.isDisplayOn()) || (lockDevice && !deviceWrapper.isDisplayOn())

        when {
            requiresNoStatusChange -> outputDisplayStatus(false)
            requiresStatusChange   -> {
                deviceWrapper.executeShellCommandWithOutput(INPUT_PRESS_POWER_BUTTON)
                outputDisplayStatus(true)
            }
        }
    }

    private fun outputDisplayStatus(displayStatusChanged: Boolean) {
        val message = "Screen of device ${deviceWrapper.getDetails()}"
        when {
            !lockDevice && displayStatusChanged  -> println("$message activated & unlocked.")
            !lockDevice && !displayStatusChanged -> println("$message already activated.")
            lockDevice && displayStatusChanged   -> println("$message deactivated & locked.")
            lockDevice && !displayStatusChanged  -> println("$message already deactivated.")
        }
    }
}
