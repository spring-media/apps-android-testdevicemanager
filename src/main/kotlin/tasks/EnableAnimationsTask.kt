package tasks

import internal.AnimationScalesPersistenceHelper
import internal.DeviceCommunicator
import internal.DeviceWrapper
import internal.TaskInfo.GROUP_NAME
import internal.devicesCanBeFound
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import tasks.internal.AnimationScalesSwitch


open class EnableAnimationsTask : DefaultTask() {

    init {
        group = GROUP_NAME
        description = "Enables animations for connected devices."
    }

    lateinit var communicator: DeviceCommunicator
    lateinit var persistenceHelper: AnimationScalesPersistenceHelper
    lateinit var animationScalesSwitch: AnimationScalesSwitch

    @TaskAction
    fun enableAnimations() {
        val bridge = communicator.bridge
        val outputReceiverProvider = communicator.outputReceiverProvider

        bridge.devicesCanBeFound()

        if (!persistenceHelper.hasOutputDir()) throw GradleException("Output directory cannot be found.")
        if (!persistenceHelper.hasConfigFile()) throw GradleException("Config file cannot be found.")

        bridge.devices.forEach { device ->
            val deviceWrapper = DeviceWrapper(device, outputReceiverProvider)
            animationScalesSwitch.deviceWrapper = deviceWrapper
            animationScalesSwitch.enableAnimations()
        }

        persistenceHelper.deleteConfigFile()
        persistenceHelper.deleteOutputDir()
    }
}