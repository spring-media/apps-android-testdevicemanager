package tasks

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
import com.android.sdklib.AndroidVersion
import com.nhaarman.mockito_kotlin.*
import internal.DeviceCommunicator
import internal.OutputReceiverProvider
import internal.ShellCommands.DUMPSYS_INPUT_METHOD
import internal.ShellCommands.INPUT_PRESS_POWER_BUTTON
import internal.ShellCommands.INPUT_SLEEP_CALL
import internal.TaskNames.LOCK_TASK_NAME
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.internal.verification.Times
import tasks.internal.BaseTest
import java.io.File

class LockDeviceTaskTest : BaseTest() {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    val device: IDevice = mock()
    val bridge: AndroidDebugBridge = mock()
    val outputReceiver: CollectingOutputReceiver = mock()
    val outputReceiverProvider: OutputReceiverProvider = mock()

    val deviceCommunicator = DeviceCommunicator(bridge, outputReceiverProvider)
    val devices = arrayOf(device)
    val displayOn = "mScreenOn=true"
    val emptyString = ""
    val apiLevel20 = AndroidVersion("20")
    val apiLevel19 = AndroidVersion("19")

    lateinit var projectDir: File
    lateinit var project: Project
    lateinit var task: LockDeviceTask

    @Before
    fun setup() {
        projectDir = temporaryFolder.root
        projectDir.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        task = project.tasks.create(LOCK_TASK_NAME, LockDeviceTask::class.java)
        task.communicator = deviceCommunicator

        given(bridge.devices).willReturn(devices)
        given(outputReceiverProvider.get()).willReturn(outputReceiver)
    }

    @Test
    fun `display can be deactivated with sdk version equal twenty`() {
        given(device.version).willReturn(apiLevel20)
        given(outputReceiver.output).willReturn(emptyString)

        task.runTaskFor(device)

        then(device).should().executeShellCommand(eq(INPUT_SLEEP_CALL), any())
        thenDeviceShouldGetDetails(device)
    }

    @Test
    fun `display can be deactivated with sdk version smaller than twenty`() {
        given(device.version).willReturn(apiLevel19)
        given(outputReceiver.output).willReturn(displayOn)

        task.runTaskFor(device)

        then(device).should(Times(2)).executeShellCommand(eq(DUMPSYS_INPUT_METHOD), any())
        then(device).should().executeShellCommand(eq(INPUT_PRESS_POWER_BUTTON), any())
        thenDeviceShouldGetDetails(device)
    }
}