package tasks.internal

import internal.*

class AnimationScalesSwitch(private val persistenceHelper: AnimationScalesPersistenceHelper) {

    lateinit var deviceWrapper: DeviceWrapper

    private lateinit var androidId: String
    private lateinit var currentDeviceValues: HashMap<String, Float>

    private val animationScaleValuesZero = createAnimationsScalesWithValue(0F)
    private val animationScaleValuesOne = createAnimationsScalesWithValue(1F)

    fun enableAnimations() {

        updateDeviceValues()

        when {
            currentDeviceValues.haveNoZeros() && persistenceHelper.hasOneEntryForId(androidId)   -> {
                println("Animations are already enabled for ${deviceWrapper.getDetails()}")
                deviceWrapper.printAnimationValues()
            }
            !currentDeviceValues.haveNoZeros() && persistenceHelper.hasOneEntryForId(androidId)  -> {
                val valuesToRestore = persistenceHelper.getValuesForDevice(androidId)
                deviceWrapper.setAnimationValues(valuesToRestore)
                deviceWrapper.printAnimationValues()
            }
            currentDeviceValues.haveNoZeros() && !persistenceHelper.hasOneEntryForId(androidId)  -> {
                println("Animations are already enabled for ${deviceWrapper.getDetails()}")
                deviceWrapper.printAnimationValues()
            }
            !currentDeviceValues.haveNoZeros() && !persistenceHelper.hasOneEntryForId(androidId) -> {
                deviceWrapper.setAnimationValues(animationScaleValuesOne)
                deviceWrapper.printAnimationValues()
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

    private fun setValuesToZero(deviceWrapper: DeviceWrapper) {
        deviceWrapper.setAnimationValues(animationScaleValuesZero)
        deviceWrapper.printAnimationValues()
    }

}
