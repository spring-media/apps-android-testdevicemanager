package tasks

import internal.DeviceCommunicator
import internal.ShellCommands.DUMPSYS_INPUT_METHOD
import internal.ShellCommands.INPUT_PRESS_POWER_BUTTON
import internal.ShellCommands.INPUT_WAKE_UP_CALL
import TestDeviceManagerPlugin.Companion.UNLOCK_TASK_NAME
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
import com.android.sdklib.AndroidVersion
import com.nhaarman.mockito_kotlin.*
import internal.OutputReceiverProvider
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.internal.verification.Times
import java.io.File

class UnlockDeviceTaskTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    val device: IDevice = mock()
    val bridge: AndroidDebugBridge = mock()
    val outputReceiver: CollectingOutputReceiver = mock()
    val outputReceiverProvider: OutputReceiverProvider = mock()

    val deviceCommunicator = DeviceCommunicator(bridge, outputReceiverProvider)
    val noDevices = emptyArray<IDevice>()
    val devices = arrayOf(device)
    val unlockMethod = "power button"
    val password = "test"
    val pin = "1111"
    val apiLevel20 = AndroidVersion("20")
    val apiLevel19 = AndroidVersion("19")
    val displayOn = "mScreenOn=true"
    val emptyString = ""

    lateinit var projectDir: File
    lateinit var project: Project
    lateinit var task: UnlockDeviceTask

    @Before
    fun setup() {
        projectDir = temporaryFolder.root
        projectDir.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        task = project.tasks.create(UNLOCK_TASK_NAME, UnlockDeviceTask::class.java)
        task.communicator = deviceCommunicator
        task.unlockBy = unlockMethod
        task.password = password
        task.pin = pin

        given(bridge.devices).willReturn(devices)
        given(outputReceiverProvider.get()).willReturn(outputReceiver)
    }

    @Test(expected = GradleException::class)
    fun `throw gradle exception when no devices connected`() {
        given(bridge.devices).willReturn(noDevices)

        task.unlock()
    }

    @Test
    fun `display can be activated by power button press for api level smaller twenty when display is off`() {
        given(device.version).willReturn(apiLevel19)
        given(outputReceiver.output).willReturn(emptyString)

        task.unlock()

        then(device).should(Times(1)).executeShellCommand(eq(DUMPSYS_INPUT_METHOD), any())
        then(device).should(Times(1)).executeShellCommand(eq(INPUT_PRESS_POWER_BUTTON), any())
    }

    @Test
    fun `display will not be activated by power button press for api level smaller twenty when display is on`() {
        given(device.version).willReturn(apiLevel19)
        given(outputReceiver.output).willReturn(displayOn)

        task.unlock()

        then(device).should(Times(1)).executeShellCommand(eq(DUMPSYS_INPUT_METHOD), any())
        then(device).should(never()).executeShellCommand(eq(INPUT_PRESS_POWER_BUTTON), any())
    }

    @Test
    fun `wake up call sent for api level twenty`() {
        given(device.version).willReturn(apiLevel20)
        given(outputReceiver.output).willReturn(emptyString)

        task.unlock()

        then(device).should().executeShellCommand(eq(INPUT_WAKE_UP_CALL), any())
    }
}
