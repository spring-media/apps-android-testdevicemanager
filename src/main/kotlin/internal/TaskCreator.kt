package internal

import TestDeviceManagerExtension
import internal.TaskNames.ANIMATIONS_DISABLE_TASK_NAME
import internal.TaskNames.ANIMATIONS_ENABLE_TASK_NAME
import internal.TaskNames.CHECK_LANGUAGE_TASK_NAME
import internal.TaskNames.CHECK_WIFI_TASK_NAME
import internal.TaskNames.LOCK_TASK_NAME
import internal.TaskNames.SET_LANGUAGE_TASK_NAME
import internal.TaskNames.STAY_AWAKE_DISABLE_TASK_NAME
import internal.TaskNames.STAY_AWAKE_ENABLE_TASK_NAME
import internal.TaskNames.UNLOCK_TASK_NAME
import internal.Tasks.*
import org.gradle.api.Project
import org.gradle.api.Task
import tasks.*
import tasks.internal.AnimationScalesSwitch
import tasks.internal.DefaultPluginTask

object TaskNames {
    const val UNLOCK_TASK_NAME = "connectedDeviceUnlock"
    const val LOCK_TASK_NAME = "connectedDeviceLock"
    const val ANIMATIONS_DISABLE_TASK_NAME = "connectedAnimationsDisable"
    const val ANIMATIONS_ENABLE_TASK_NAME = "connectedAnimationsEnable"
    const val STAY_AWAKE_ENABLE_TASK_NAME = "connectedStayAwakeEnable"
    const val STAY_AWAKE_DISABLE_TASK_NAME = "connectedStayAwakeDisable"
    const val CHECK_WIFI_TASK_NAME = "connectedCheckWifi"
    const val CHECK_LANGUAGE_TASK_NAME = "connectedCheckLanguage"
    const val SET_LANGUAGE_TASK_NAME = "connectedSetLanguage"
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
    object CHECK_LANGUAGE : Tasks<CheckLanguageTask>(CHECK_LANGUAGE_TASK_NAME, CheckLanguageTask::class.java)
    object SET_LANGUAGE : Tasks<SetLanguageTask>(SET_LANGUAGE_TASK_NAME, SetLanguageTask::class.java)
}

class TaskCreator(
        private val project: Project,
        private val extension: TestDeviceManagerExtension,
        private val communicator: DeviceCommunicator,
        private val animationScalesPersistenceHelper: AnimationScalesPersistenceHelper,
        private val animationScalesSwitch: AnimationScalesSwitch
) {

    fun createTasks() {
        createTask(UNLOCK) {
            it.unlockBy = extension.unlockBy
            it.pin = extension.pin
            it.password = extension.password
        }

        createTask(LOCK)
        createTask(ANIMATION_DISABLE) {
            it.persistenceHelper = animationScalesPersistenceHelper
            it.animationScalesSwitch = animationScalesSwitch
        }

        createTask(ANIMATION_ENABLE) {
            it.persistenceHelper = animationScalesPersistenceHelper
            it.animationScalesSwitch = animationScalesSwitch
        }

        createTask(STAY_AWAKE_ENABLE)
        createTask(STAY_AWAKE_DISABLE)
        createTask(CHECK_WIFI_CONNECTION) {
            it.wifi = extension.wifi
        }

        createTask(CHECK_LANGUAGE) {
            it.language = extension.language
        }
        createTask(SET_LANGUAGE) {
            it.language = extension.language
        }
    }

    private fun <T : DefaultPluginTask> createTask(task: Tasks<T>, configuration: ((T) -> Unit)? = null) {
        project.tasks.create(task.name, task.type)  {
            it.communicator = communicator
            configuration?.invoke(it as T)
        }
    }
}