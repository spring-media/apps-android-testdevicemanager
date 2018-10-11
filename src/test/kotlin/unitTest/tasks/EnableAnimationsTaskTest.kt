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
import tasks.EnableAnimationsTask
import tasks.internal.AnimationScalesSwitch
import java.io.File


class EnableAnimationsTaskTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    val device: IDevice = mock()
    val bridge: AndroidDebugBridge = mock()
    val outputReceiverProvider: OutputReceiverProvider = mock()
    val persistenceHelper: AnimationScalesPersistenceHelper = mock()
    val animationsScalesSwitch: AnimationScalesSwitch = mock()

    val deviceCommunicator = DeviceCommunicator(bridge, outputReceiverProvider)
    val noDevices = emptyArray<IDevice>()
    val devices = arrayOf(device)

    lateinit var projectDir: File
    lateinit var project: Project
    lateinit var task: EnableAnimationsTask

    @Before
    fun setup() {
        projectDir = temporaryFolder.root
        projectDir.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        task = project.tasks.create("EnableAnimationsTask", EnableAnimationsTask::class.java)

        task.communicator = deviceCommunicator
        task.persistenceHelper = persistenceHelper
        task.animationScalesSwitch = animationsScalesSwitch

        given(bridge.devices).willReturn(devices)
        given(persistenceHelper.hasOutputDir()).willReturn(true)
        given(persistenceHelper.hasConfigFile()).willReturn(true)
    }

    @Test(expected = GradleException::class)
    fun `throw gradle exception when no devices connected`() {
        given(bridge.devices).willReturn(noDevices)

        task.enableAnimations()
    }

    @Test(expected = GradleException::class)
    fun `gradle exception is thrown when outDir does not exist`() {
        given(persistenceHelper.hasOutputDir()).willReturn(false)

        task.enableAnimations()
    }

    @Test(expected = GradleException::class)
    fun `gradle exception is thrown when config file does not exist`() {
        given(persistenceHelper.hasConfigFile()).willReturn(false)

        task.enableAnimations()
    }

    @Test
    fun `can check for persistence`() {
        task.enableAnimations()

        then(persistenceHelper).should().hasOutputDir()
        then(persistenceHelper).should().hasConfigFile()
    }

    @Test
    fun `can enable animations via animationsScaleSwitch`() {
        task.enableAnimations()

        then(animationsScalesSwitch).should().enableAnimations()
    }

    @Test
    fun `can delete config file`() {
        task.enableAnimations()

        then(persistenceHelper).should(Times(1)).deleteConfigFile()
    }

    @Test
    fun `can delete output directory`() {
        task.enableAnimations()

        then(persistenceHelper).should(Times(1)).deleteOutputDir()
    }
}