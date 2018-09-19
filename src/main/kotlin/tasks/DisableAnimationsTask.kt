package tasks

import animationSettings
import com.android.ddmlib.AndroidDebugBridge
import devicesCanBeFound
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import printAnimationValue
import setAnimationValue

open class DisableAnimationsTask : DefaultTask() {

    init {
        group = "device setup"
        description = "Disables animations for connected devices."
    }

    @Input
    lateinit var bridge: AndroidDebugBridge

    @Input
    var disableAnimations: Boolean = false

    @TaskAction
    fun disableAnimations() {
        if (disableAnimations) {
            bridge.devicesCanBeFound()

            bridge.devices.forEach { device ->

                //todo kr: get animation values from device and save them in a file

                animationSettings.forEach {
                    device.setAnimationValue(it, 0)
                    device.printAnimationValue(it)
                }
            }
        } else {
            println("Disabling animations was not activated in build script.")
        }
    }
}