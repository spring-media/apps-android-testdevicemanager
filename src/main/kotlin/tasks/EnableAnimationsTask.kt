package tasks

import AnimationScalesPersistenceHelper
import AnimationsScales
import TestDeviceManagerPlugin.Companion.GROUP_NAME
import com.android.ddmlib.AndroidDebugBridge
import details
import devicesCanBeFound
import getAndroidId
import getAnimationValues
import hasNoZeros
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import printAnimationValues
import setAnimationValues
import java.io.File


open class EnableAnimationsTask : DefaultTask() {

    init {
        group = GROUP_NAME
        description = "Enables animations for connected devices."
    }

    @Input
    lateinit var bridge: AndroidDebugBridge

    @OutputFile
    lateinit var configFile: File

    @OutputDirectory
    lateinit var outDir: File

    private lateinit var valuesToRestore: AnimationsScales

    @TaskAction
    fun enableAnimations() {
        bridge.devicesCanBeFound()

        val persistenceHelper = AnimationScalesPersistenceHelper(outDir, configFile)

        if (!outDir.exists()) {
            throw GradleException("Output directory cannot be found.")
        }
        if (!configFile.exists()) {
            throw GradleException("Config file cannot be found.")
        }

        bridge.devices.forEach { device ->
            val androidId = device.getAndroidId()
            val currentDeviceValues = device.getAnimationValues()

            when {
                currentDeviceValues.hasNoZeros() && persistenceHelper.hasOneEntryForId(androidId)   -> {
                    println("Animations are already enabled for ${device.details()}")
                    device.printAnimationValues()
                }
                !currentDeviceValues.hasNoZeros() && persistenceHelper.hasOneEntryForId(androidId)  -> {
                    valuesToRestore = persistenceHelper.getValuesForDevice(androidId)
                    device.setAnimationValues(valuesToRestore)
                    device.printAnimationValues()
                }
                currentDeviceValues.hasNoZeros() && !persistenceHelper.hasOneEntryForId(androidId)  -> {
                    println("Animations are already enabled for ${device.details()}")
                    device.printAnimationValues()
                }
                !currentDeviceValues.hasNoZeros() && !persistenceHelper.hasOneEntryForId(androidId) -> {
                    valuesToRestore = AnimationsScales(1F, 1F, 1F)
                    device.setAnimationValues(valuesToRestore)
                    device.printAnimationValues()
                }
            }
        }

        configFile.delete()
        outDir.delete()
    }
}