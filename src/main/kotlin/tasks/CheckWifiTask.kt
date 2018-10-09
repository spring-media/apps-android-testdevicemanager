package tasks

import internal.DeviceCommunicator
import internal.DeviceWrapper
import internal.TaskInfo.GROUP_NAME
import internal.devicesCanBeFound
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction


open class CheckWifiTask : DefaultTask() {

    init {
        group = GROUP_NAME
        description = "check if a connection to a specific wifi was established"
    }

    @Input
    lateinit var wifi: String

    @Input
    lateinit var communicator: DeviceCommunicator

    @TaskAction
    fun checkWifi() {
        val bridge = communicator.bridge
        val provider = communicator.outputReceiverProvider

        if (!wifi.isBlank()) {
            bridge.devicesCanBeFound()

            bridge.devices.forEach { device ->
                val deviceWrapper = DeviceWrapper(device, provider)
                deviceWrapper.checkWifi(wifi)
                println("Device ${deviceWrapper.getDetails()} is connected to $wifi.")
            }
        } else {
            throw GradleException("No name for wifi maintained in build script.")
        }
    }
}