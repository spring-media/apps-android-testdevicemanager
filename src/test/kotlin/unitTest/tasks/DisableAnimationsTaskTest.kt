package unitTest.tasks

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import internal.AnimationScalesPersistenceHelper
import internal.DeviceCommunicator
import internal.OutputReceiverProvider
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.internal.verification.Times
import tasks.DisableAnimationsTask
import tasks.internal.AnimationScalesSwitch
import java.io.File


class DisableAnimationsTaskTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    private val device: IDevice = mock()
    private val bridge: AndroidDebugBridge = mock()
    private val outputReceiverProvider: OutputReceiverProvider = mock()
    private val persistenceHelper: AnimationScalesPersistenceHelper = mock()
    private val animationScalesSwitch: AnimationScalesSwitch = mock()

    private val deviceCommunicator = DeviceCommunicator(bridge, outputReceiverProvider)
    private val noDevices = emptyArray<IDevice>()
    private val devices = arrayOf(device)

    lateinit var projectDir: File
    lateinit var project: Project
    lateinit var task: DisableAnimationsTask

    @Before
    fun setup() {
        projectDir = temporaryFolder.root
        projectDir.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        task = project.tasks.create("DisableAnimationsTask", DisableAnimationsTask::class.java)

        task.communicator = deviceCommunicator
        task.persistenceHelper = persistenceHelper
        task.animationScalesSwitch = animationScalesSwitch

        given(bridge.devices).willReturn(devices)
        given(persistenceHelper.hasOutputDir()).willReturn(true)
        given(persistenceHelper.hasConfigFile()).willReturn(true)
    }

    @Test(expected = GradleException::class)
    fun `throw gradle exception when no devices connected`() {
        given(bridge.devices).willReturn(noDevices)

        task.disableAnimations()
    }

    @Test
    fun `can check persistence`() {

        task.disableAnimations()

        then(persistenceHelper).should().hasOutputDir()
        then(persistenceHelper).should().hasConfigFile()
    }

    @Test
    fun `output directory can be created`() {
        given(persistenceHelper.hasOutputDir()).willReturn(false)

        task.disableAnimations()

        then(persistenceHelper).should(Times(1)).createOutputDirectory()
    }

    @Test
    fun `config file can be created`() {
        given(persistenceHelper.hasConfigFile()).willReturn(false)

        task.disableAnimations()

        then(persistenceHelper).should(Times(1)).createConfigFile()
    }

    @Test
    fun `animations can be disabled by animationScalesSwitch`() {
        task.disableAnimations()

        then(animationScalesSwitch).should().disableAnimations()
    }
}