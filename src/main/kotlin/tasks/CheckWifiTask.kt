package tasks

import com.android.ddmlib.IDevice
import internal.DeviceWrapper
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import tasks.internal.DefaultPluginTask


open class CheckWifiTask : DefaultPluginTask() {

    @Input
    lateinit var wifi: String

    init {
        description = "check if a connection to a specific wifi was established"
    }

    override fun runTask1() {
        if (wifi.isBlank()) {
            throw GradleException("No name for wifi maintained in build script.")
        }
    }

    override fun runTask2(device: IDevice) {
        val deviceWrapper = DeviceWrapper(device, outputReceiverProvider)
        deviceWrapper.checkWifi(wifi)
        println("Device ${deviceWrapper.getDetails()} is connected to $wifi.")
    }

    override fun runTask3() {}
}