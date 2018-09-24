package tasks

import TestDeviceManagerPlugin.Companion.GROUP_NAME
import checkWifi
import com.android.ddmlib.AndroidDebugBridge
import details
import devicesCanBeFound
import org.gradle.api.DefaultTask
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
    lateinit var bridge: AndroidDebugBridge

    @TaskAction
    fun checkWifi() {
        if (!wifi.isBlank()) {
            bridge.devicesCanBeFound()

            bridge.devices.forEach {
                it.checkWifi(wifi)
                println("Device ${it.details()} is connected to $wifi.")
            }
        } else {
            error("No name for wifi maintained in build script.")
        }
    }
}