package tasks.internal

import internal.DeviceWrapper
import internal.ShellCommands.DUMPSYS_WINDOW
import internal.ShellCommands.INPUT_PRESS_ENTER
import internal.ShellCommands.INPUT_TEXT
import internal.UnlockMethods.*
import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
import com.nhaarman.mockito_kotlin.*
import internal.OutputReceiverProvider
import org.gradle.api.GradleException
import org.junit.Before
import org.junit.Test
import org.mockito.internal.verification.Times

class UnlockerTest {

    val device: IDevice = mock()
    val outputReceiver: CollectingOutputReceiver = mock()
    val outputReceiverProvider: OutputReceiverProvider = mock()

    val deviceWrapper = DeviceWrapper(device, outputReceiverProvider)

    var pin = "1111"
    var password = "password"
    var wrongPassword1 = " "
    var wrongPassword2 = ""
    val emptyString = ""
    val wrongPin1 = "111"
    val wrongPin2 = "11aa"
    val mUnrestrictedScreen = "mUnrestrictedScreen=(0,0) 100x200"
    val mFocusedWindow = "mFocusedWindow=Window{7045664 u0 StatusBar}"
    val output = "$mUnrestrictedScreen + $mFocusedWindow"
    val wrongMethod = "wrong method"

    @Before
    fun setup() {
        given(outputReceiverProvider.get()).willReturn(outputReceiver)
    }

    @Test
    fun `nothing is done when power button is chosen`() {
        val classToTest = Unlocker(
                deviceWrapper,
                POWER_BUTTON.string,
                pin,
                password
        )

        classToTest.unlock()

        then(device).should(never()).executeShellCommand(any(), any())
    }

    @Test
    fun `device can be unlocked by swipe`() {
        val classToTest = Unlocker(
                deviceWrapper,
                SWIPE.string,
                pin,
                password
        )

        given(outputReceiver.output).willReturn(mUnrestrictedScreen)

        classToTest.unlock()

        then(device).should().executeShellCommand(eq(DUMPSYS_WINDOW), any())
        then(device).should().executeShellCommand(eq("input swipe 50 160 80 40"), any())
    }

    @Test(expected = GradleException::class)
    fun `gradle exception is thrown when resolution cannot be retrieved from device`() {
        val classToTest = Unlocker(
                deviceWrapper,
                SWIPE.string,
                pin,
                password
        )

        given(outputReceiver.output).willReturn(emptyString)

        classToTest.unlock()
    }

    @Test(expected = GradleException::class)
    fun `gradle exception is thrown when pin is blank`() {
        val classToTest = Unlocker(
                deviceWrapper,
                PIN.string,
                emptyString,
                password
        )

        classToTest.unlock()
    }

    @Test(expected = GradleException::class)
    fun `gradle exception is thrown when pin has less than 4 digits`() {
        val classToTest = Unlocker(
                deviceWrapper,
                PIN.string,
                wrongPin1,
                password
        )

        classToTest.unlock()
    }

    @Test(expected = GradleException::class)
    fun `gradle exception is thrown when pin is not only numbers`() {
        val classToTest = Unlocker(
                deviceWrapper,
                PIN.string,
                wrongPin2,
                password
        )

        classToTest.unlock()
    }

    @Test
    fun `device can be unlocked by pin`() {
        val classToTest = Unlocker(
                deviceWrapper,
                PIN.string,
                pin,
                password
        )

        given(outputReceiver.output).willReturn(output)

        classToTest.unlock()

        thenPassPhraseShouldBeEntered(device, pin)
    }

    private fun thenPassPhraseShouldBeEntered(device: IDevice, passPhrase: String){
        then(device).should(Times(2)).executeShellCommand(eq(DUMPSYS_WINDOW), any())
        then(device).should().executeShellCommand(eq("input swipe 50 160 80 40"), any())
        then(device).should().executeShellCommand(eq("$INPUT_TEXT $passPhrase"), any())
        then(device).should().executeShellCommand(eq(INPUT_PRESS_ENTER), any())
    }

    @Test(expected = GradleException::class)
    fun `gradle exception is thrown when password is blank`() {
        val classToTest = Unlocker(
                deviceWrapper,
                PASSWORD.string,
                pin,
                wrongPassword1
        )

        given(outputReceiver.output).willReturn(output)

        classToTest.unlock()
    }

    @Test(expected = GradleException::class)
    fun `gradle exception is thrown when password is empty`() {
        val classToTest = Unlocker(
                deviceWrapper,
                PASSWORD.string,
                pin,
                wrongPassword2
        )

        given(outputReceiver.output).willReturn(output)

        classToTest.unlock()
    }


    @Test
    fun `device can be unlocked by password`() {
        val classToTest = Unlocker(
                deviceWrapper,
                PASSWORD.string,
                pin,
                password
        )

        given(outputReceiver.output).willReturn(output)

        classToTest.unlock()


        thenPassPhraseShouldBeEntered(device, password)
    }

    @Test(expected = GradleException::class)
    fun `gradle exception is thrown when wrong unlock method is chosen`() {
        val classToTest = Unlocker(
                deviceWrapper,
                wrongMethod,
                pin,
                password
        )

        given(outputReceiver.output).willReturn(output)

        classToTest.unlock()
    }
}