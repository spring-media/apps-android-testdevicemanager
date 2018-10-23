package unitTest.internal

import TestDeviceManagerExtension
import com.nhaarman.mockito_kotlin.*
import internal.AnimationScalesPersistenceHelper
import internal.DeviceCommunicator
import internal.TaskCreator
import org.gradle.api.Project
import org.gradle.api.internal.tasks.DefaultTaskContainer
import org.junit.Test
import tasks.*
import tasks.internal.AnimationScalesSwitch

class TaskCreatorTest {

    val project: Project = mock()
    val extension: TestDeviceManagerExtension = mock()
    val communicator: DeviceCommunicator = mock()
    val animationScalesPersistenceHelper: AnimationScalesPersistenceHelper = mock()
    val animationScalesSwitch: AnimationScalesSwitch = mock()
    val taskContainer: DefaultTaskContainer = mock()

    val classToTest = TaskCreator(
            project,
            extension,
            communicator,
            animationScalesPersistenceHelper,
            animationScalesSwitch
    )

    @Test
    fun `can create tasks`() {
        given(project.tasks).willReturn(taskContainer)

        classToTest.createTasks()

        then(taskContainer).should().create(eq("connectedDeviceUnlock"), eq(UnlockDeviceTask::class.java), any())
        then(taskContainer).should().create(eq("connectedDeviceLock"), eq(LockDeviceTask::class.java), any())
        then(taskContainer).should().create(eq("connectedAnimationsDisable"),
                                            eq(DisableAndPersistAnimationsTask::class.java),
                                            any())
        then(taskContainer).should().create(eq("connectedAnimationsEnable"),
                                            eq(EnableAnimationsTask::class.java),
                                            any())
        then(taskContainer).should().create(eq("connectedStayAwakeEnable"), eq(EnableStayAwakeTask::class.java), any())
        then(taskContainer).should().create(eq("connectedStayAwakeDisable"),
                                            eq(DisableStayAwakeTask::class.java),
                                            any())
        then(taskContainer).should().create(eq("connectedCheckWifi"), eq(CheckWifiTask::class.java), any())
    }
}