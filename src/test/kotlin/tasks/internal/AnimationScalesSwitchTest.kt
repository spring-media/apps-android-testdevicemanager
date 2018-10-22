package tasks.internal

import com.nhaarman.mockito_kotlin.*
import internal.AnimationScalesPersistenceHelper
import internal.DeviceWrapper
import internal.createAnimationsScalesWithValue
import org.junit.Before
import org.junit.Test
import org.mockito.internal.verification.Times

class AnimationScalesSwitchTest {

    private val persistenceHelper: AnimationScalesPersistenceHelper = mock()
    private val deviceWrapper: DeviceWrapper = mock()

    private val classToTest = AnimationScalesSwitch(persistenceHelper)

    private val androidId = "androidId"
    private val animationValues0 = createAnimationsScalesWithValue(0F)
    private val animationValues1 = createAnimationsScalesWithValue(1F)
    private val animationValues2 = createAnimationsScalesWithValue(2F)

    @Before
    fun setup() {
        classToTest.deviceWrapper = deviceWrapper

        given(deviceWrapper.getAndroidId()).willReturn(androidId)
        given(deviceWrapper.getAnimationValues()).willReturn(animationValues0)
    }

    @Test
    fun `can get android id for device when enabling animations`() {
        classToTest.enableAnimations()

        then(deviceWrapper).should().getAndroidId()
    }

    @Test
    fun `can get animation values for device when enabling animations`() {
        classToTest.enableAnimations()

        then(deviceWrapper).should().getAnimationValues()
    }

    @Test
    fun `device values have no zeros and persistence has one entry`() {
        given(deviceWrapper.getAnimationValues()).willReturn(animationValues2)
        given(persistenceHelper.hasOneEntryForId(androidId)).willReturn(true)

        classToTest.enableAnimations()

        thenOutputAnimationValues()
        then(persistenceHelper).should(never()).getValuesForDevice(any())
        then(deviceWrapper).should(never()).setAnimationValues(any())
    }

    @Test
    fun `device values have zeros and persistence has one entry`() {
        given(deviceWrapper.getAnimationValues()).willReturn(animationValues0)
        given(persistenceHelper.hasOneEntryForId(androidId)).willReturn(true)

        given(persistenceHelper.getValuesForDevice(androidId)).willReturn(animationValues2)
        val valuesToRestore = persistenceHelper.getValuesForDevice(androidId)

        classToTest.enableAnimations()

        then(deviceWrapper).should(never()).getDetails()
        then(persistenceHelper).should(Times(2)).getValuesForDevice(androidId)
        then(deviceWrapper).should().setAnimationValues(valuesToRestore)
        then(deviceWrapper).should().printAnimationValues()
    }

    @Test
    fun `device values have no zeros and persistence has no entry`() {
        given(deviceWrapper.getAnimationValues()).willReturn(animationValues2)
        given(persistenceHelper.hasOneEntryForId(androidId)).willReturn(false)

        classToTest.enableAnimations()

        thenOutputAnimationValues()
    }

    @Test
    fun `device values have zeros and persistence has no entry`() {
        given(deviceWrapper.getAnimationValues()).willReturn(animationValues0)
        given(persistenceHelper.hasOneEntryForId(androidId)).willReturn(false)

        classToTest.enableAnimations()

        then(persistenceHelper).should(never()).getValuesForDevice(any())
        then(deviceWrapper).should(never()).getDetails()
        then(deviceWrapper).should().setAnimationValues(animationValues1)
        then(deviceWrapper).should().printAnimationValues()
    }

    @Test
    fun `can get animation values when disabling animations`() {

        classToTest.disableAnimations()

        then(deviceWrapper).should().getAndroidId()
    }

    @Test
    fun `can get animation values for device when disabling animations`() {
        classToTest.disableAnimations()

        then(deviceWrapper).should().getAnimationValues()
    }

    @Test
    fun `device values are all zero and persistence has one entry`() {
        given(persistenceHelper.hasOneEntryForId(androidId)).willReturn(true)

        classToTest.disableAnimations()

        then(persistenceHelper).should(never()).deleteEntryForId(any())
        then(persistenceHelper).should(never()).appendTextToConfigFileForId(any(), any())
        then(deviceWrapper).should(never()).setAnimationValues(animationValues0)
        then(deviceWrapper).should(never()).printAnimationValues()
        then(deviceWrapper).should().getDetails()
    }

    @Test
    fun `device values are all zero and persistence has no entry`() {
        given(persistenceHelper.hasOneEntryForId(androidId)).willReturn(false)

        classToTest.disableAnimations()

        then(persistenceHelper).should(never()).deleteEntryForId(any())
        then(persistenceHelper).should().appendTextToConfigFileForId(androidId, animationValues1)
        then(deviceWrapper).should(never()).setAnimationValues(animationValues0)
        then(deviceWrapper).should(never()).printAnimationValues()
        then(deviceWrapper).should().getDetails()
    }

    @Test
    fun `device values are not all zero and persistence has one entry`() {
        given(deviceWrapper.getAnimationValues()).willReturn(animationValues2)
        given(persistenceHelper.hasOneEntryForId(androidId)).willReturn(true)

        classToTest.disableAnimations()

        then(persistenceHelper).should().deleteEntryForId(androidId)
        then(persistenceHelper).should().appendTextToConfigFileForId(androidId, animationValues2)
        then(deviceWrapper).should().setAnimationValues(animationValues0)
        then(deviceWrapper).should().printAnimationValues()
        then(deviceWrapper).should(never()).getDetails()
    }

    @Test
    fun `device values are not all zero and persistence has no entry`() {
        given(deviceWrapper.getAnimationValues()).willReturn(animationValues2)
        given(persistenceHelper.hasOneEntryForId(androidId)).willReturn(false)

        classToTest.disableAnimations()

        then(persistenceHelper).should(never()).deleteEntryForId(any())
        then(persistenceHelper).should().appendTextToConfigFileForId(androidId, animationValues2)
        then(deviceWrapper).should().setAnimationValues(animationValues0)
        then(deviceWrapper).should().printAnimationValues()
        then(deviceWrapper).should(never()).getDetails()
    }

    private fun thenOutputAnimationValues() {
        then(deviceWrapper).should().getDetails()
        then(deviceWrapper).should().printAnimationValues()
    }
}