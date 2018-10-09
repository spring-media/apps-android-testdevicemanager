package tasks

import internal.DeviceCommunicator
import internal.DeviceWrapper
import internal.StayAwakeStatus.STAY_AWAKE
import internal.StayAwakeStatus.STAY_NOT_AWAKE
import internal.TaskInfo.GROUP_NAME
import internal.devicesCanBeFound
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction


open class EnableStayAwakeTask : DefaultTask() {

    init {
        group = GROUP_NAME
        description = "Activate the Stay Awake settings in the developer options."
    }

    @Input
    lateinit var communicator: DeviceCommunicator

    @TaskAction
    fun enableStayAwake() {
        val bridge = communicator.bridge
        val provider = communicator.outputReceiverProvider

        bridge.devicesCanBeFound()

        bridge.devices.forEach { device ->
            val deviceWrapper = DeviceWrapper(device, provider)

            if (deviceWrapper.getStayAwakeStatus() == STAY_NOT_AWAKE.value) {
                deviceWrapper.setStayAwakeStatus(STAY_AWAKE)
                println("Device ${deviceWrapper.getDetails()} will stay awake.")
            } else {
                println("Staying awake was already enabled for ${deviceWrapper.getDetails()}")
            }
        }
    }
}