import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import internal.AnimationScalesPersistenceHelper
import internal.DeviceCommunicator
import internal.OutputReceiverProvider
import internal.createAndroidDebugBridge
import org.gradle.api.Plugin
import org.gradle.api.Project
import tasks.*
import tasks.internal.AnimationScalesSwitch
import java.io.File

class TestDeviceManagerPlugin : Plugin<Project> {

    companion object {
        const val CHECK_WIFI_TASK_NAME = "connectedCheckWifi"
        const val EXTENSION_NAME = "testDeviceManager"
        const val UNLOCK_TASK_NAME = "connectedDeviceUnlock"
        const val LOCK_TASK_NAME = "connectedDeviceLock"
        const val ANIMATIONS_DISABLE_TASK_NAME = "connectedAnimationsDisable"
        const val ANIMATIONS_ENABLE_TASK_NAME = "connectedAnimationsEnable"
        const val STAY_AWAKE_ENABLE_TASK_NAME = "connectedStayAwakeEnable"
        const val STAY_AWAKE_DISABLE_TASK_NAME = "connectedStayAwakeDisable"
        const val OUTPUT_DIRECTORY_PATH = "/generated/source/testDeviceManager"
        const val CONFIG_FILE_PATH = "/generated/source/testDeviceManager/configFile.txt"
    }

    private lateinit var project: Project
    private lateinit var androidAppExtension: AppExtension
    private lateinit var bridge: AndroidDebugBridge
    private lateinit var extension: TestDeviceManagerExtension
    private lateinit var outDir: File
    private lateinit var configFile: File
    private lateinit var communicator: DeviceCommunicator
    private lateinit var outputReceiverProvider: OutputReceiverProvider
    private lateinit var animationScalesPersistenceHelper: AnimationScalesPersistenceHelper
    private lateinit var animationScalesSwitch: AnimationScalesSwitch

    override fun apply(target: Project) {
        extension = target.extensions.create(EXTENSION_NAME, TestDeviceManagerExtension::class.java)

        project = target

        target.afterEvaluate { project ->
            project.allprojects { subProject ->
                androidAppExtension = subProject.extensions.getByType(AppExtension::class.java)

                bridge = androidAppExtension.createAndroidDebugBridge()
                outputReceiverProvider = OutputReceiverProvider()
                communicator = DeviceCommunicator(bridge, outputReceiverProvider)

                outDir = File(project.buildDir, OUTPUT_DIRECTORY_PATH)
                configFile = File(project.buildDir, CONFIG_FILE_PATH)
                animationScalesPersistenceHelper = AnimationScalesPersistenceHelper(outDir, configFile)
                animationScalesSwitch = AnimationScalesSwitch(animationScalesPersistenceHelper)

                createTasks()
            }
        }
    }

    private fun createTasks() {
        project.tasks.create(UNLOCK_TASK_NAME, UnlockDeviceTask::class.java) {
            it.communicator = communicator
            it.unlockBy = extension.unlockBy
            it.pin = extension.pin
            it.password = extension.password
        }

        project.tasks.create(LOCK_TASK_NAME, LockDeviceTask::class.java) {
            it.communicator = communicator
        }

        project.tasks.create(ANIMATIONS_DISABLE_TASK_NAME, DisableAnimationsTask::class.java) {
            it.communicator = communicator
            it.persistenceHelper = animationScalesPersistenceHelper
            it.animationScalesSwitch = animationScalesSwitch
        }

        project.tasks.create(ANIMATIONS_ENABLE_TASK_NAME, EnableAnimationsTask::class.java) {
            it.communicator = communicator
            it.persistenceHelper = animationScalesPersistenceHelper
            it.animationScalesSwitch = animationScalesSwitch
        }

        project.tasks.create(STAY_AWAKE_ENABLE_TASK_NAME, EnableStayAwakeTask::class.java) {
            it.communicator = communicator
        }

        project.tasks.create(STAY_AWAKE_DISABLE_TASK_NAME, DisableStayAwakeTask::class.java) {
            it.communicator = communicator
        }

        project.tasks.create(CHECK_WIFI_TASK_NAME, CheckWifiTask::class.java) {
            it.communicator = communicator
            it.wifi = extension.wifi
        }
    }
}
