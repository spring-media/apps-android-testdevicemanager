package tasks.internal

import com.android.ddmlib.IDevice
import internal.DeviceWrapper
import internal.LockStatus
import internal.LockStatus.LOCK_DEVICE
import internal.LockStatus.UNLOCK_DEVICE
import internal.ShellCommands.INPUT_PRESS_POWER_BUTTON
import internal.ShellCommands.INPUT_SLEEP_CALL
import internal.ShellCommands.INPUT_WAKE_UP_CALL
import tasks.UnlockDeviceTask.Companion.ANDROID_API_LEVEL_44W

open class SetLockStatusTask(private val status: LockStatus) : DefaultPluginTask() {

    private lateinit var deviceWrapper: DeviceWrapper

    override fun runTask1() {}

    override fun runTask2() {}

    override fun runTaskFor(device: IDevice) {
        deviceWrapper = DeviceWrapper(device, outputReceiverProvider)
        when (status) {
            LOCK_DEVICE   -> {
                setDisplayStatusOf(deviceWrapper)
            }
            UNLOCK_DEVICE -> {
                setDisplayStatusOf(deviceWrapper)
            }
        }
    }

    override fun runPostTask() {}

    private fun setDisplayStatusOf(deviceWrapper: DeviceWrapper) {
        val device = deviceWrapper.device

        if (device.version.isGreaterOrEqualThan(ANDROID_API_LEVEL_44W)) {
            if (status == UNLOCK_DEVICE) {
                deviceWrapper.executeShellCommandWithOutput(INPUT_WAKE_UP_CALL)
                outputDisplayStatus(true)
            } else {
                deviceWrapper.executeShellCommandWithOutput(INPUT_SLEEP_CALL)
                outputDisplayStatus(true)
            }
        } else {
            when {
                status == UNLOCK_DEVICE && deviceWrapper.isDisplayOn()  -> {
                    outputDisplayStatus(false)
                }
                status == UNLOCK_DEVICE && !deviceWrapper.isDisplayOn() -> {
                    deviceWrapper.executeShellCommandWithOutput(INPUT_PRESS_POWER_BUTTON)
                    outputDisplayStatus(true)
                }
                status == LOCK_DEVICE && deviceWrapper.isDisplayOn()    -> {
                    deviceWrapper.executeShellCommandWithOutput(INPUT_PRESS_POWER_BUTTON)
                    outputDisplayStatus(true)
                }
                status == LOCK_DEVICE && !deviceWrapper.isDisplayOn()   -> {
                    outputDisplayStatus(false)
                }
            }
        }
    }

    private fun outputDisplayStatus(displayStatusChanged: Boolean) {
        val message = "Screen of device ${deviceWrapper.getDetails()}"
        when {
            status == UNLOCK_DEVICE && displayStatusChanged  -> println("$message activated & unlocked.")
            status == UNLOCK_DEVICE && !displayStatusChanged -> println("$message already activated.")
            status == LOCK_DEVICE && displayStatusChanged    -> println("$message deactivated & locked.")
            status == LOCK_DEVICE && !displayStatusChanged   -> println("$message already deactivated.")
        }
    }
}
