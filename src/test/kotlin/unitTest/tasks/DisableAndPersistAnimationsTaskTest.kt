package unitTest.tasks

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.then
import internal.AnimationScalesPersistenceHelper
import internal.DeviceCommunicator
import internal.OutputReceiverProvider
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import tasks.DisableAndPersistAnimationsTask
import tasks.internal.AnimationScalesSwitch
import java.io.File


class DisableAndPersistAnimationsTaskTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    private val device: IDevice = mock()
    private val bridge: AndroidDebugBridge = mock()
    private val outputReceiverProvider: OutputReceiverProvider = mock()
    private val persistenceHelper: AnimationScalesPersistenceHelper = mock()
    private val animationScalesSwitch: AnimationScalesSwitch = mock()

    private val deviceCommunicator = DeviceCommunicator(bridge, outputReceiverProvider)
    private val devices = arrayOf(device)

    lateinit var projectDir: File
    lateinit var project: Project
    lateinit var task: DisableAndPersistAnimationsTask

    @Before
    fun setup() {
        projectDir = temporaryFolder.root
        projectDir.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        task = project.tasks.create("DisableAnimationsTask", DisableAndPersistAnimationsTask::class.java)

        task.communicator = deviceCommunicator
        task.persistenceHelper = persistenceHelper
        task.animationScalesSwitch = animationScalesSwitch

        given(bridge.devices).willReturn(devices)
        given(persistenceHelper.hasConfigFile()).willReturn(true)
    }

    @Test
    fun `can check persistence`() {
        task.runTask1()

        then(persistenceHelper).should().hasConfigFile()
    }

    @Test
    fun `output directory can be created`() {
        task.runTask1()

        then(persistenceHelper).should(never()).createConfigFileInPath()
    }

    @Test
    fun `config file can be created`() {
        given(persistenceHelper.hasConfigFile()).willReturn(false)

        task.runTask1()

        then(persistenceHelper).should().createConfigFileInPath()
    }

    @Test
    fun `animations can be disabled by animationScalesSwitch`() {
        task.runTask2(device)

        then(animationScalesSwitch).should().disableAnimations()
    }

    @Test
    fun `should check for config file in task 3`() {
        task.runTask3()

        then(persistenceHelper).should().hasConfigFile()
    }

}