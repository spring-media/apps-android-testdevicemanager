package tasks

import internal.AnimationScalesPersistenceHelper
import internal.DeviceCommunicator
import internal.DeviceWrapper
import internal.TaskInfo.GROUP_NAME
import internal.devicesCanBeFound
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import tasks.internal.AnimationScalesSwitch

open class DisableAnimationsTask : DefaultTask() {

    init {
        group = GROUP_NAME
        description = "Disables animations for connected devices."
    }

    lateinit var communicator: DeviceCommunicator
    lateinit var persistenceHelper: AnimationScalesPersistenceHelper
    lateinit var animationScalesSwitch: AnimationScalesSwitch

    @TaskAction
    fun disableAnimations() {
        val bridge = communicator.bridge
        val outputReceiverProvider = communicator.outputReceiverProvider

        bridge.devicesCanBeFound()

        if (!persistenceHelper.hasOutputDir()) persistenceHelper.createOutputDirectory()
        if (!persistenceHelper.hasConfigFile()) persistenceHelper.createConfigFile()

        bridge.devices.forEach { device ->
            val deviceWrapper = DeviceWrapper(device, outputReceiverProvider)
            animationScalesSwitch.deviceWrapper = deviceWrapper
            animationScalesSwitch.disableAnimations()
        }
    }
}