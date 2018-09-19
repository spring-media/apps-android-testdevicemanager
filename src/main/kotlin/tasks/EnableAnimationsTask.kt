package tasks

import animationSettings
import com.android.ddmlib.AndroidDebugBridge
import devicesCanBeFound
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import printAnimationValue
import setAnimationValue


open class EnableAnimationsTask : DefaultTask() {

    init {
        group = "device setup"
        description = "Enables animations for connected devices."
    }

    @Input
    lateinit var bridge: AndroidDebugBridge

    @Input
    var enableAnimations: Boolean = false

    @TaskAction
    fun enableAnimations() {
        if (enableAnimations) {
            bridge.devicesCanBeFound()

            //todo kr: read animation values from file compare with current value and start enabling if they differ

            bridge.devices.forEach { device ->
                animationSettings.forEach {
                    device.setAnimationValue(it, 1)
                    device.printAnimationValue(it)
                }

            }
        } else {
            println("Enabling animations was not activated in build script.")
        }
    }
}