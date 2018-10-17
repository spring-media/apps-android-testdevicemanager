package tasks.internal

import com.android.ddmlib.IDevice
import internal.DeviceWrapper
import internal.StayAwakeStatus
import internal.StayAwakeStatus.STAY_AWAKE
import internal.StayAwakeStatus.STAY_NOT_AWAKE


open class SetStayAwakeStatusTask(private val status: StayAwakeStatus) : DefaultPluginTask() {

    override fun runTask1() {}
    override fun runTask2() {}

    override fun runTaskFor(device: IDevice) {
        val deviceWrapper = DeviceWrapper(device, outputReceiverProvider)

        if (deviceWrapper.getStayAwakeStatus() != status.value) {
            deviceWrapper.setStayAwakeStatus(status)
            setSuccessMessageFor(status, deviceWrapper)
        } else {
            setFailureMessage(status, deviceWrapper)
        }
    }

    override fun runPostTask() {}

    private fun setSuccessMessageFor(status: StayAwakeStatus, deviceWrapper: DeviceWrapper) {
        val successMessage = "Device ${deviceWrapper.getDetails()} will"
        when (status) {
            STAY_AWAKE     -> println("$successMessage stay awake.")
            STAY_NOT_AWAKE -> println("$successMessage not stay awake anymore.")
        }
    }

    private fun setFailureMessage(status: StayAwakeStatus, deviceWrapper: DeviceWrapper) {
        val failureMessagePart1 = "Staying awake was already"
        val failureMessagePart2 = "for ${deviceWrapper.getDetails()}"
        when (status) {
            STAY_AWAKE     -> println("$failureMessagePart1 enabled $failureMessagePart2")
            STAY_NOT_AWAKE -> println("$failureMessagePart1 disabled $failureMessagePart2")
        }
    }
}