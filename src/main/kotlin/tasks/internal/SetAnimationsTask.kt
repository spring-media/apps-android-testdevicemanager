package tasks.internal

import com.android.ddmlib.IDevice
import internal.AnimationScalesPersistenceHelper
import internal.DeviceWrapper
import org.gradle.api.GradleException


open class SetAnimationsTask(private val enableAnimations: Boolean) : DefaultPluginTask() {

    lateinit var persistenceHelper: AnimationScalesPersistenceHelper
    lateinit var animationScalesSwitch: AnimationScalesSwitch

    override fun runTask1() {
        val hasDirectory = persistenceHelper.hasOutputDir()
        val hasConfigFile = persistenceHelper.hasConfigFile()

        if (enableAnimations) {
            if (!hasDirectory) throw GradleException("Output directory cannot be found.")
            if (!hasConfigFile) throw GradleException("Config file cannot be found.")
        } else {
            if (!hasDirectory) persistenceHelper.createOutputDirectory()
            if (!hasConfigFile) persistenceHelper.createConfigFile()
        }
    }

    override fun runTask2(device: IDevice) {
        animationScalesSwitch.deviceWrapper = DeviceWrapper(device, outputReceiverProvider)

        if (enableAnimations)
            animationScalesSwitch.enableAnimations()
        else
            animationScalesSwitch.disableAnimations()
    }

    override fun runTask3() {
        if (enableAnimations) {
            persistenceHelper.deleteConfigFile()
            persistenceHelper.deleteOutputDir()
        }
    }

}


