package tasks.internal

import internal.*

class AnimationScalesSwitch(private val persistenceHelper: AnimationScalesPersistenceHelper) {

    lateinit var deviceWrapper: DeviceWrapper

    private lateinit var androidId: String
    private lateinit var currentDeviceValues: LinkedHashMap<String, Float>

    private val animationScaleValuesZero = createAnimationsScalesWithValue(scaleValueZero)
    private val animationScaleValuesOne = createAnimationsScalesWithValue(scaleValueOne)

    fun enableAnimations() {

        updateDeviceValues()

        when {
            currentDeviceValues.hasNoZeros() && persistenceHelper.hasOneEntryForId(androidId)   -> {
                outputAnimationValues()
            }
            !currentDeviceValues.hasNoZeros() && persistenceHelper.hasOneEntryForId(androidId)  -> {
                val valuesToRestore = persistenceHelper.getValuesForDevice(androidId)
                deviceWrapper.setAnimationValues(valuesToRestore)
                deviceWrapper.printAnimationValues()
            }
            currentDeviceValues.hasNoZeros() && !persistenceHelper.hasOneEntryForId(androidId)  -> {
                outputAnimationValues()
            }
            !currentDeviceValues.hasNoZeros() && !persistenceHelper.hasOneEntryForId(androidId) -> {
                setAndOutputAnimationValues()
            }
            currentDeviceValues.hasNoZeros() && !persistenceHelper.hasOutputDir() -> {
                outputAnimationValues()
            }
            currentDeviceValues.hasNoZeros() && !persistenceHelper.hasConfigFile() -> {
                outputAnimationValues()
            }
            !currentDeviceValues.hasNoZeros() && !persistenceHelper.hasOutputDir() -> {
                setAndOutputAnimationValues()
            }
            !currentDeviceValues.hasNoZeros() && !persistenceHelper.hasConfigFile() -> {
                setAndOutputAnimationValues()
            }
        }
    }

    fun disableAnimations() {

        updateDeviceValues()

        when {
            currentDeviceValues.areAllZero() && persistenceHelper.hasOneEntryForId(androidId)   -> {
                println("Animations already disabled for ${deviceWrapper.getDetails()}")
            }
            currentDeviceValues.areAllZero() && !persistenceHelper.hasOneEntryForId(androidId)  -> {
                persistenceHelper.appendTextToConfigFileForId(androidId, animationScaleValuesOne)
                println("Animations already disabled for ${deviceWrapper.getDetails()}")
            }
            !currentDeviceValues.areAllZero() && persistenceHelper.hasOneEntryForId(androidId)  -> {
                persistenceHelper.deleteEntryForId(androidId)
                persistenceHelper.appendTextToConfigFileForId(androidId, currentDeviceValues)
                setValuesToZero(deviceWrapper)
            }
            !currentDeviceValues.areAllZero() && !persistenceHelper.hasOneEntryForId(androidId) -> {
                persistenceHelper.appendTextToConfigFileForId(androidId, currentDeviceValues)
                setValuesToZero(deviceWrapper)
            }
        }
    }

    private fun updateDeviceValues() {
        androidId = deviceWrapper.getAndroidId()
        currentDeviceValues = deviceWrapper.getAnimationValues()
    }

    private fun outputAnimationValues() {
        println("Animations are already enabled for ${deviceWrapper.getDetails()}")
        deviceWrapper.printAnimationValues()
    }

    private fun setAndOutputAnimationValues() {
        deviceWrapper.setAnimationValues(animationScaleValuesOne)
        deviceWrapper.printAnimationValues()
    }

    private fun setValuesToZero(deviceWrapper: DeviceWrapper) {
        deviceWrapper.setAnimationValues(animationScaleValuesZero)
        deviceWrapper.printAnimationValues()
    }

}
