package tasks

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
import com.nhaarman.mockito_kotlin.*
import internal.DeviceCommunicator
import internal.OutputReceiverProvider
import internal.ShellCommands.SETTINGS_PUT_STAY_ON
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import unitTest.tasks.internal.BaseUnitTest
import java.io.File

class DisableStayAwakeTaskUnitTest : BaseUnitTest() {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    val device: IDevice = mock()
    val bridge: AndroidDebugBridge = mock()
    val outputReceiver: CollectingOutputReceiver = mock()
    val outputReceiverProvider: OutputReceiverProvider = mock()

    val deviceCommunicator = DeviceCommunicator(bridge, outputReceiverProvider)
    val devices = arrayOf(device)
    val deviceStaysNotAwake = "0"
    val deviceStaysAwake = "2"

    lateinit var projectDir: File
    lateinit var project: Project
    lateinit var task: DisableStayAwakeTask

    @Before
    fun setup() {
        projectDir = temporaryFolder.root
        projectDir.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        task = project.tasks.create("DisableStayAwakeTask", DisableStayAwakeTask::class.java)

        task.communicator = deviceCommunicator

        given(outputReceiverProvider.get()).willReturn(outputReceiver)
    }

    @Test
    fun `only get device details when device is already not staying awake`() {
        given(bridge.devices).willReturn(devices)
        given(outputReceiver.output).willReturn(deviceStaysNotAwake)

        task.runTask2(device)

        then(device).should(never()).executeShellCommand(eq("$SETTINGS_PUT_STAY_ON $deviceStaysNotAwake"), any())
        thenDeviceShouldGetDetails(device)
    }

    @Test
    fun `set awake status and get device details when device is already not staying awake`() {
        given(bridge.devices).willReturn(devices)
        given(outputReceiver.output).willReturn(deviceStaysAwake)

        task.runTask2(device)

        then(device).should().executeShellCommand(eq("$SETTINGS_PUT_STAY_ON $deviceStaysNotAwake"), any())
        thenDeviceShouldGetDetails(device)
    }
}