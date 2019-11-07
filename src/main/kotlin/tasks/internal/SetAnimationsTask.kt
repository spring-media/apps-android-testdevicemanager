package tasks.internal

import com.android.ddmlib.IDevice
import internal.AnimationScalesPersistenceHelper
import internal.DeviceWrapper


open class SetAnimationsTask(private val enableAnimations: Boolean) : DefaultPluginTask() {

    lateinit var persistenceHelper: AnimationScalesPersistenceHelper
    lateinit var animationScalesSwitch: AnimationScalesSwitch


    override fun runTask1() {
        val hasDirectory = persistenceHelper.hasOutputDir()
        val hasConfigFile = persistenceHelper.hasConfigFile()
        if (enableAnimations) {
            if (!hasDirectory)  println("Output directory cannot be found - using default values.")
            if (!hasConfigFile) println("Config file cannot be found - using default values.")
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
        val hasDirectory = persistenceHelper.hasOutputDir()
        val hasConfigFile = persistenceHelper.hasConfigFile()
        if (enableAnimations && hasDirectory && hasConfigFile) {
            persistenceHelper.deleteConfigFile()
            persistenceHelper.deleteOutputDir()
        }
    }

}


