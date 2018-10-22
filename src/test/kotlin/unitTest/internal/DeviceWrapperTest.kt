package internal

import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import com.winterbe.expekt.should
import internal.StayAwakeStatus.STAY_AWAKE
import internal.StayAwakeStatus.STAY_NOT_AWAKE
import org.gradle.api.GradleException
import org.junit.Before
import org.junit.Test
import org.mockito.internal.verification.Times
import tasks.internal.BaseTest

class DeviceWrapperTest : BaseTest() {

    val device: IDevice = mock()
    val outputReceiverProvider: OutputReceiverProvider = mock()
    val outputReceiver: CollectingOutputReceiver = mock()

    val screenOn = "mScreenOn=true"
    val screenOff = "mScreenOn=false"
    val mUnrestrictedScreen = "mUnrestrictedScreen=(0,0) 1080x1920"
    val mFocusedWindowLocked = "mFocusedWindow=Window{1aaa111 u0 StatusBar}"
    val mFocusedWindowUnloocked = "mFocusedWindow=Window{a11aa1a u0 com.android.settings}"
    val wifi = "wifi"
    val mNetworkInfo = "mNetworkInfo [type: WIFI[] - WIFI, state: CONNECTED/CONNECTED, reason: (unspecified), extra: \"wifi\""
    val mNetworkInfo2 = "mNetworkInfo [type: WIFI[] - WIFI, state: CONNECTED/CONNECTED, reason: (unspecified), extra: \"wifi2\""
    val stayAwake = "2"
    val stayNotAwake = "0"
    val settingsGetStayOn = "settings get global stay_on_while_plugged_in"
    val settingsPutStayOn = "settings put global stay_on_while_plugged_in"
    val settingsGetAndroidId = "settings get secure android_id"
    val settingsGetWindowAnimationScale = "settings get global window_animation_scale"
    val settingsGetTransitionAnimationScale = "settings get global transition_animation_scale"
    val settingsGetAnimatorDurationScale = "settings get global animator_duration_scale"
    val settingsPutWindowAnimationScale = "settings put global window_animation_scale 1.0"
    val settingsPutTransitionAnimationScale = "settings put global transition_animation_scale 1.0"
    val settingsPutAnimatorDurationScale = "settings put global animator_duration_scale 1.0"
    val androidId = "androidId"
    val animationsScales = createAnimationsScalesWithValue(1F)
    val dumpsysInputMethod = "dumpsys input_method"
    val dumpSysWindow = "dumpsys window"
    val dumpSysWifi = "dumpsys wifi"

    val classToTest = DeviceWrapper(
            device,
            outputReceiverProvider
    )

    @Before
    fun setup() {
        given(outputReceiverProvider.get()).willReturn(outputReceiver)
    }

    @Test
    fun `check if display is on can be true`() {
        given(outputReceiver.output).willReturn(screenOn)

        val result = classToTest.isDisplayOn()

        deviceShouldExecuteShellCommand(dumpsysInputMethod, 1)
        result.should.be.`true`
    }

    @Test
    fun `check if display is on can be false`() {
        given(outputReceiver.output).willReturn(screenOff)

        val result = classToTest.isDisplayOn()

        deviceShouldExecuteShellCommand(dumpsysInputMethod, 1)
        result.should.be.`false`
    }

    @Test
    fun `can get details`() {
        classToTest.getDetails()

        thenDeviceShouldGetDetails(device)
    }

    @Test
    fun `can get screen resolution`() {
        given(outputReceiver.output).willReturn(mUnrestrictedScreen)

        val result = classToTest.getDeviceScreenResolution()

        deviceShouldExecuteShellCommand(dumpSysWindow, 1)
        result.xValue.should.equal(1080)
        result.yValue.should.equal(1920)
    }


    @Test(expected = GradleException::class)
    fun `gradle exception can be thrown when getting screen resolution and regex cannot find it`() {
        given(outputReceiver.output).willReturn("")

        classToTest.getDeviceScreenResolution()
    }

    @Test
    fun `isDeviceUnlocked can return false if device is locked`() {
        given(outputReceiver.output).willReturn(mFocusedWindowLocked)

        val result = classToTest.isDeviceUnlocked()

        deviceShouldExecuteShellCommand(dumpSysWindow, 1)
        result.should.be.`false`
    }

    @Test
    fun `isDeviceUnlocked can return true if device is unlocked`() {
        given(outputReceiver.output).willReturn(mFocusedWindowUnloocked)

        val result = classToTest.isDeviceUnlocked()

        deviceShouldExecuteShellCommand(dumpSysWindow, 1)
        result.should.be.`true`
    }

    @Test(expected = GradleException::class)
    fun `gradle exception can be thrown when connected to wrong wifi`() {
        given(outputReceiver.output).willReturn(mNetworkInfo2)

        classToTest.checkWifi(wifi)
    }

    @Test
    fun `can check for connected wifi`() {
        given(outputReceiver.output).willReturn(mNetworkInfo)

        classToTest.checkWifi(wifi)

        deviceShouldExecuteShellCommand(dumpSysWifi, 1)
    }

    @Test
    fun `can get stay awake status`() {
        given(outputReceiver.output).willReturn(stayAwake)

        val result = classToTest.getStayAwakeStatus()

        deviceShouldExecuteShellCommand(settingsGetStayOn, 1)
        result.should.equal(2)
    }

    @Test
    fun `can set stay awake status to stay awake`() {
        given(outputReceiver.output).willReturn("")

        classToTest.setStayAwakeStatus(STAY_AWAKE)

        then(device).should().executeShellCommand("$settingsPutStayOn $stayAwake", outputReceiver)
    }

    @Test
    fun `can set stay awake status to stay not awake`() {
        given(outputReceiver.output).willReturn("")

        classToTest.setStayAwakeStatus(STAY_NOT_AWAKE)

        then(device).should().executeShellCommand("$settingsPutStayOn $stayNotAwake", outputReceiver)
    }

    @Test
    fun `can get android id`() {
        given(outputReceiver.output).willReturn(androidId)

        val result = classToTest.getAndroidId()

        deviceShouldExecuteShellCommand(settingsGetAndroidId, 1)
        result.should.equal(androidId)
    }

    @Test
    fun `can get animation values`() {
        given(outputReceiver.output).willReturn("1")

        val result = classToTest.getAnimationValues()

        thenAnimationScalesShouldBeGathered()
        result.should.equal(animationsScales)
    }

    @Test
    fun `can set animation values`() {
        given(outputReceiver.output).willReturn("")

        classToTest.setAnimationValues(animationsScales)

        deviceShouldExecuteShellCommand(settingsPutWindowAnimationScale)
        deviceShouldExecuteShellCommand(settingsPutTransitionAnimationScale)
        deviceShouldExecuteShellCommand(settingsPutAnimatorDurationScale)
    }

    @Test
    fun `can print animation values`() {
        given(outputReceiver.output).willReturn("1")

        classToTest.printAnimationValues()

        thenAnimationScalesShouldBeGathered()
        thenDeviceShouldGetDetails(device,  3)
    }

    @Test
    fun `can execute shell command with output`() {
        given(outputReceiver.output).willReturn(stayAwake)

        val result = classToTest.executeShellCommandWithOutput(settingsPutStayOn)

        then(device).should().executeShellCommand(settingsPutStayOn, outputReceiver)
        result.should.equal(stayAwake)
    }

    private fun thenAnimationScalesShouldBeGathered(){
        deviceShouldExecuteShellCommand(settingsGetWindowAnimationScale)
        deviceShouldExecuteShellCommand(settingsGetTransitionAnimationScale)
        deviceShouldExecuteShellCommand(settingsGetAnimatorDurationScale)
    }

    private fun deviceShouldExecuteShellCommand(command: String, times: Int = 1) {
        then(device).should(Times(times)).executeShellCommand(command, outputReceiver)
    }
}
