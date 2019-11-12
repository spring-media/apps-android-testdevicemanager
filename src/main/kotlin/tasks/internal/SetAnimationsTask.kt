package tasks.internal

import com.android.ddmlib.IDevice
import internal.AnimationScalesPersistenceHelper
import internal.DeviceWrapper


open class SetAnimationsTask(private val enableAnimations: Boolean) : DefaultPluginTask() {

    lateinit var persistenceHelper: AnimationScalesPersistenceHelper
    lateinit var animationScalesSwitch: AnimationScalesSwitch


    override fun runTask1() {
        val hasConfigFile = persistenceHelper.hasConfigFile()
        if (enableAnimations) {
            if (!hasConfigFile) println("Config file cannot be found - using default values.")
        } else {
            if (!hasConfigFile) persistenceHelper.createConfigFileInPath()
        }
    }

    override fun runTask2(device: IDevice) {
        animationScalesSwitch.deviceWrapper = DeviceWrapper(device, outputReceiverProvider)

        if (enableAnimations) {
            animationScalesSwitch.enableAnimations()
        }
        else {
            animationScalesSwitch.disableAnimations()
        }
    }

    override fun runTask3() {
        val hasConfigFile = persistenceHelper.hasConfigFile()

        if (enableAnimations && hasConfigFile) {
            persistenceHelper.deleteConfigFile()
        }
    }

}


