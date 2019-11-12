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
        given(persistenceHelper.hasConfigFile()).willReturn(true)
    }

    @Test
    fun `can check for persistence`() {
        task.runTask1()

        then(persistenceHelper).should().hasConfigFile()
    }

    @Test
    fun `can enable animations via animationsScaleSwitch`() {
        task.runTask2(device)

        then(animationsScalesSwitch).should().enableAnimations()
    }

    @Test
    fun `can delete config file`() {
        task.runTask3()

        then(persistenceHelper).should().deleteConfigFile()
    }

    @Test
    fun `can delete output directory`() {
        task.runTask3()

        then(persistenceHelper).should().deleteConfigFile()
    }

    @Test
    fun `will not delete configFile when there is no config file`() {
        given(persistenceHelper.hasConfigFile()).willReturn(false)

        task.runTask3()

        then(persistenceHelper).should(never()).deleteConfigFile()
    }
}