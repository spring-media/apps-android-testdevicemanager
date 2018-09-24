import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import org.gradle.api.Plugin
import org.gradle.api.Project
import tasks.*
import java.io.File

class TestDeviceManagerPlugin : Plugin<Project> {

    companion object {
        const val GROUP_NAME = "device setup"
    }

    private lateinit var project: Project
    private lateinit var androidAppExtension: AppExtension
    private lateinit var bridge: AndroidDebugBridge
    private lateinit var extension: TestDeviceManagerExtension
    private lateinit var outDir: File
    private lateinit var configFile: File


    override fun apply(target: Project) {
        extension = target.extensions.create("deviceUnlocker", TestDeviceManagerExtension::class.java)

        project = target

        target.afterEvaluate { project ->
            project.allprojects { subProject ->
                androidAppExtension = subProject.extensions.getByType(AppExtension::class.java)
                bridge = androidAppExtension.createAndroidDebugBridge()
                outDir = File(project.buildDir, "/generated/source/testDeviceManager")
                configFile = File(project.buildDir, "/generated/source/testDeviceManager/configFile.txt")

                createTasks()
            }
        }
    }

    private fun createTasks() {
        project.tasks.create("connectedDeviceUnlock", UnlockDeviceTask::class.java) {
            it.android = androidAppExtension
            it.bridge = bridge
            it.unlockBy = extension.unlockBy
            it.pin = extension.pin
            it.password = extension.password
        }

        project.tasks.create("connectedDeviceLock", LockDeviceTask::class.java) {
            it.android = androidAppExtension
            it.bridge = bridge
        }

        project.tasks.create("connectedAnimationsDisable", DisableAnimationsTask::class.java) {
            it.bridge = bridge
            it.configFile = configFile
            it.outDir = outDir
        }

        project.tasks.create("connectedAnimationsEnable", EnableAnimationsTask::class.java) {
            it.bridge = bridge
            it.configFile = configFile
            it.outDir = outDir
        }

        project.tasks.create("connectedStayAwakeEnable", EnableStayAwakeTask::class.java) {
            it.bridge = bridge
        }

        project.tasks.create("connectedStayAwakeDisable", DisableStayAwakeTask::class.java) {
            it.bridge = bridge
        }

        project.tasks.create("connectedCheckWifi", CheckWifiTask::class.java) {
            it.wifi = extension.wifi
            it.bridge = bridge
        }
    }
}
