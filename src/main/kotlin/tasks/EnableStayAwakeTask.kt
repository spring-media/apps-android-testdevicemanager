package tasks

import TestDeviceManagerPlugin.Companion.GROUP_NAME
import com.android.ddmlib.AndroidDebugBridge
import details
import devicesCanBeFound
import getStayAwakeStatus
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import setStayAwakeStatus


open class EnableStayAwakeTask : DefaultTask() {

    init {
        group = GROUP_NAME
        description = "Activate the Stay Awake settings in the developer options."
    }

    @Input
    lateinit var bridge: AndroidDebugBridge

    @TaskAction
    fun enableStayAwake() {
        bridge.devicesCanBeFound()

        bridge.devices.forEach {
            if (it.getStayAwakeStatus() == 0) {
                it.setStayAwakeStatus(true)
                println("Device ${it.details()} will stay awake.")
            } else {
                println("Staying awake was already enabled for ${it.details()}")
            }
        }
    }
}