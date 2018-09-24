package tasks

import AnimationScalesPersistenceHelper
import AnimationsScales
import TestDeviceManagerPlugin.Companion.GROUP_NAME
import areAllZero
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import details
import devicesCanBeFound
import getAndroidId
import getAnimationValues
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import printAnimationValues
import setAnimationValues
import java.io.File

open class DisableAnimationsTask : DefaultTask() {

    init {
        group = GROUP_NAME
        description = "Disables animations for connected devices."
    }

    @Input
    lateinit var bridge: AndroidDebugBridge

    @OutputFile
    lateinit var configFile: File

    @OutputDirectory
    lateinit var outDir: File

    private val animationScaleValuesSetToOne = AnimationsScales(1F, 1F, 1F)

    @TaskAction
    fun disableAnimations() {
        bridge.devicesCanBeFound()

        val persistenceHelper = AnimationScalesPersistenceHelper(outDir, configFile)
        if (!outDir.exists()) persistenceHelper.createOutputDirectory()
        if (!configFile.exists()) persistenceHelper.createConfigFile()

        bridge.devices.forEach { device ->
            val androidId = device.getAndroidId()
            val currentDeviceValues = device.getAnimationValues()

            when {
                currentDeviceValues.areAllZero() && persistenceHelper.hasOneEntryForId(androidId)   -> {
                    println("Animations already disabled for ${device.details()}")
                }
                !persistenceHelper.hasOneEntryForId(androidId) && currentDeviceValues.areAllZero()  -> {
                    configFile = persistenceHelper.appendTextToConfigFileForId(androidId, animationScaleValuesSetToOne)
                    println("Animations already disabled for ${device.details()}")
                }
                persistenceHelper.hasOneEntryForId(androidId) && !currentDeviceValues.areAllZero()  -> {
                    configFile = persistenceHelper.deleteEntryForId(androidId)
                    configFile = persistenceHelper.appendTextToConfigFileForId(androidId, currentDeviceValues)
                    setValuesToZero(device)
                }
                !persistenceHelper.hasOneEntryForId(androidId) && !currentDeviceValues.areAllZero() -> {
                    configFile = persistenceHelper.appendTextToConfigFileForId(androidId, currentDeviceValues)
                    setValuesToZero(device)
                }
            }
        }
    }

    private fun setValuesToZero(device: IDevice) {
        device.setAnimationValues(AnimationsScales(0F, 0F, 0F))
        device.printAnimationValues()
    }
}