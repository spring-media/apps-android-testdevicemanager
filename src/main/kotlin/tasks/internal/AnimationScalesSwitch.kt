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
                setAndOutputAnimationValues(valuesToRestore)
            }
            currentDeviceValues.hasNoZeros() && !persistenceHelper.hasOneEntryForId(androidId)  -> {
                outputAnimationValues()
            }
            !currentDeviceValues.hasNoZeros() && !persistenceHelper.hasOneEntryForId(androidId) -> {
                setAndOutputAnimationValues(animationScaleValuesOne)
            }
            currentDeviceValues.hasNoZeros() && !persistenceHelper.hasConfigFile() -> {
                outputAnimationValues()
            }
            !currentDeviceValues.hasNoZeros() && !persistenceHelper.hasConfigFile() -> {
                setAndOutputAnimationValues(animationScaleValuesOne)
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
        deviceWrapper.printAnimationValues()
    }

    private fun setAndOutputAnimationValues(values: LinkedHashMap<String, Float>) {
        deviceWrapper.setAnimationValues(values)
        deviceWrapper.printAnimationValues()
    }

    private fun setValuesToZero(deviceWrapper: DeviceWrapper) {
        deviceWrapper.setAnimationValues(animationScaleValuesZero)
        deviceWrapper.printAnimationValues()
    }
}
