package internal

import TestDeviceManagerExtension
import internal.TaskNames.ANIMATIONS_DISABLE_TASK_NAME
import internal.TaskNames.ANIMATIONS_ENABLE_TASK_NAME
import internal.TaskNames.CHECK_WIFI_TASK_NAME
import internal.TaskNames.LOCK_TASK_NAME
import internal.TaskNames.STAY_AWAKE_DISABLE_TASK_NAME
import internal.TaskNames.STAY_AWAKE_ENABLE_TASK_NAME
import internal.TaskNames.UNLOCK_TASK_NAME
import internal.Tasks.*
import org.gradle.api.Project
import org.gradle.api.Task
import tasks.*
import tasks.internal.AnimationScalesSwitch

object TaskNames {
    const val UNLOCK_TASK_NAME = "connectedDeviceUnlock"
    const val LOCK_TASK_NAME = "connectedDeviceLock"
    const val ANIMATIONS_DISABLE_TASK_NAME = "connectedAnimationsDisable"
    const val ANIMATIONS_ENABLE_TASK_NAME = "connectedAnimationsEnable"
    const val STAY_AWAKE_ENABLE_TASK_NAME = "connectedStayAwakeEnable"
    const val STAY_AWAKE_DISABLE_TASK_NAME = "connectedStayAwakeDisable"
    const val CHECK_WIFI_TASK_NAME = "connectedCheckWifi"
}

sealed class Tasks<T : Task>(var name: String, var type: Class<T>) {
    object UNLOCK : Tasks<UnlockDeviceTask>(UNLOCK_TASK_NAME, UnlockDeviceTask::class.java)
    object LOCK : Tasks<LockDeviceTask>(LOCK_TASK_NAME, LockDeviceTask::class.java)
    object ANIMATION_DISABLE : Tasks<DisableAndPersistAnimationsTask>(ANIMATIONS_DISABLE_TASK_NAME,
                                                                      DisableAndPersistAnimationsTask::class.java)

    object ANIMATION_ENABLE : Tasks<EnableAnimationsTask>(ANIMATIONS_ENABLE_TASK_NAME, EnableAnimationsTask::class.java)
    object STAY_AWAKE_ENABLE : Tasks<EnableStayAwakeTask>(STAY_AWAKE_ENABLE_TASK_NAME, EnableStayAwakeTask::class.java)
    object STAY_AWAKE_DISABLE : Tasks<DisableStayAwakeTask>(STAY_AWAKE_DISABLE_TASK_NAME,
                                                            DisableStayAwakeTask::class.java)

    object CHECK_WIFI_CONNECTION : Tasks<CheckWifiTask>(CHECK_WIFI_TASK_NAME, CheckWifiTask::class.java)
}

class TaskCreator(
        private val project: Project,
        private val extension: TestDeviceManagerExtension,
        private val communicator: DeviceCommunicator,
        private val animationScalesPersistenceHelper: AnimationScalesPersistenceHelper,
        private val animationScalesSwitch: AnimationScalesSwitch
) {

    fun createTasks() {
        project.tasks.create(UNLOCK.name, UNLOCK.type) {
            it.communicator = communicator
            it.unlockBy = extension.unlockBy
            it.pin = extension.pin
            it.password = extension.password
        }

        project.tasks.create(LOCK.name, LOCK.type) {
            it.communicator = communicator
        }

        project.tasks.create(ANIMATION_DISABLE.name, ANIMATION_DISABLE.type) {
            it.communicator = communicator
            it.persistenceHelper = animationScalesPersistenceHelper
            it.animationScalesSwitch = animationScalesSwitch
        }

        project.tasks.create(ANIMATION_ENABLE.name, ANIMATION_ENABLE.type) {
            it.communicator = communicator
            it.persistenceHelper = animationScalesPersistenceHelper
            it.animationScalesSwitch = animationScalesSwitch
        }

        project.tasks.create(STAY_AWAKE_ENABLE.name, STAY_AWAKE_ENABLE.type) {
            it.communicator = communicator
        }

        project.tasks.create(STAY_AWAKE_DISABLE.name, STAY_AWAKE_DISABLE.type) {
            it.communicator = communicator
        }

        project.tasks.create(CHECK_WIFI_CONNECTION.name, CHECK_WIFI_CONNECTION.type) {
            it.communicator = communicator
            it.wifi = extension.wifi
        }
    }
}