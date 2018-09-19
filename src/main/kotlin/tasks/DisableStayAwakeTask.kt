package tasks

import com.android.ddmlib.AndroidDebugBridge
import details
import devicesCanBeFound
import getStayAwakeStatus
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import setStayAwakeStatus


open class DisableStayAwakeTask : DefaultTask() {

    init {
        group = "device setup"
        description = "Deactivate the Stay Awake settings in the developer options."
    }

    @Input
    lateinit var bridge: AndroidDebugBridge

    @TaskAction
    fun disableStayAwake() {
        bridge.devicesCanBeFound()

        bridge.devices.forEach {
            if (it.getStayAwakeStatus() != 0) {
                it.setStayAwakeStatus(false)
                println("Device ${it.details()} will not stay awake anymore.")
            } else {
                println("Staying awake was already disabled for ${it.details()}")
            }
        }
    }
}