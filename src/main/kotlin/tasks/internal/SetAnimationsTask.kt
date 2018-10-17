package tasks.internal

import com.android.ddmlib.IDevice
import internal.AnimationScalesPersistenceHelper
import internal.DeviceWrapper
import internal.SetAnimationsStatus
import internal.SetAnimationsStatus.DISABLE_ANIMATIONS
import internal.SetAnimationsStatus.ENABLE_ANIMATIONS
import org.gradle.api.GradleException


open class SetAnimationsTask(private val status: SetAnimationsStatus) : DefaultPluginTask() {

    lateinit var persistenceHelper: AnimationScalesPersistenceHelper
    lateinit var animationScalesSwitch: AnimationScalesSwitch

    override fun runTask1() {}

    override fun runTask2() {
        val hasDirectory = persistenceHelper.hasOutputDir()
        val hasConfigFile = persistenceHelper.hasConfigFile()

        when (status) {
            ENABLE_ANIMATIONS  -> {
                if (!hasDirectory) throw GradleException("Output directory cannot be found.")
                if (!hasConfigFile) throw GradleException("Config file cannot be found.")
            }
            DISABLE_ANIMATIONS -> {
                if (!hasDirectory) persistenceHelper.createOutputDirectory()
                if (!hasConfigFile) persistenceHelper.createConfigFile()
            }
        }
    }

    override fun runTaskFor(device: IDevice) {
        animationScalesSwitch.deviceWrapper = DeviceWrapper(device, outputReceiverProvider)

        when (status) {
            ENABLE_ANIMATIONS  -> animationScalesSwitch.enableAnimations()
            DISABLE_ANIMATIONS -> animationScalesSwitch.disableAnimations()
        }
    }

    override fun runPostTask() {
        if (status == ENABLE_ANIMATIONS) {
            persistenceHelper.deleteConfigFile()
            persistenceHelper.deleteOutputDir()
        }
    }

}


