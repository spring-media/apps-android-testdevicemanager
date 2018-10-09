package tasks

import internal.DeviceCommunicator
import internal.ShellCommands.SETTINGS_PUT_STAY_ON
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
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

class EnableStayAwakeTaskTest {

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
    val deviceStaysNotAwake = "0"
    val deviceStaysAwake = "2"

    lateinit var projectDir: File
    lateinit var project: Project
    lateinit var task: EnableStayAwakeTask

    @Before
    fun setup() {
        projectDir = temporaryFolder.root
        projectDir.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        task = project.tasks.create("EnableStayAwakeTask", EnableStayAwakeTask::class.java)

        task.communicator = deviceCommunicator

        given(outputReceiverProvider.get()).willReturn(outputReceiver)
    }

    @Test(expected = GradleException::class)
    fun `throw gradle exception when no devices connected`() {
        given(bridge.devices).willReturn(noDevices)

        task.enableStayAwake()
    }

    @Test
    fun `only get device details when device is already set to stay awake`() {
        given(bridge.devices).willReturn(devices)
        given(outputReceiver.output).willReturn(deviceStaysAwake)

        task.enableStayAwake()

        then(device).should(never()).executeShellCommand(eq("$SETTINGS_PUT_STAY_ON $deviceStaysAwake"), any())
        deviceDetailsShown()
    }

    @Test
    fun `set awake status and get device details when device is not set to awake`() {
        given(bridge.devices).willReturn(devices)
        given(outputReceiver.output).willReturn(deviceStaysNotAwake)

        task.enableStayAwake()

        then(device).should(Times(1)).executeShellCommand(eq("$SETTINGS_PUT_STAY_ON $deviceStaysAwake"), any())
        deviceDetailsShown()
    }

    private fun deviceDetailsShown() {
        then(device).should(Times(1)).getProperty("ro.product.model")
        then(device).should(Times(1)).getProperty("ro.build.version.release")
        then(device).should(Times(1)).getProperty("ro.build.version.sdk")
    }
}