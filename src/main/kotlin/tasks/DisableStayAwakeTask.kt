package tasks

import internal.DeviceCommunicator
import internal.DeviceWrapper
import internal.StayAwakeStatus.STAY_NOT_AWAKE
import internal.TaskInfo.GROUP_NAME
import internal.devicesCanBeFound
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class DisableStayAwakeTask : DefaultTask() {

    init {
        group = GROUP_NAME
        description = "Deactivate the Stay Awake settings in the developer options."
    }

    @Input
    lateinit var communicator: DeviceCommunicator

    @TaskAction
    fun disableStayAwake() {
        val bridge = communicator.bridge
        val provider = communicator.outputReceiverProvider

        bridge.devicesCanBeFound()

        bridge.devices.forEach { device ->
            val deviceWrapper = DeviceWrapper(device, provider)
            if (deviceWrapper.getStayAwakeStatus() != STAY_NOT_AWAKE.value) {
                deviceWrapper.setStayAwakeStatus(STAY_NOT_AWAKE)
                println("Device ${deviceWrapper.getDetails()} will not stay awake anymore.")
            } else {
                println("Staying awake was already disabled for ${deviceWrapper.getDetails()}")
            }
        }
    }
}