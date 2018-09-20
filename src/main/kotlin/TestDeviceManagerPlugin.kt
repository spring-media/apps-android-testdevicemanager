import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import tasks.*

class TestDeviceManagerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("deviceUnlocker", DeviceUnlockerExtension::class.java)

        target.afterEvaluate { project ->
            project.allprojects { subProject ->
                val androidAppExtension = subProject.extensions.getByType(AppExtension::class.java)
                val bridge = androidAppExtension.createAndroidDebugBridge()

                target.tasks.create("connectedDeviceUnlock", UnlockDeviceTask::class.java) {
                    it.android = androidAppExtension
                    it.bridge = bridge
                    it.unlockBy = extension.unlockBy
                    it.pin = extension.pin
                    it.password = extension.password
                }

                target.tasks.create("connectedDeviceLock", LockDeviceTask::class.java) {
                    it.android = androidAppExtension
                    it.bridge = bridge
                }

                target.tasks.create("connectedAnimationsDisable", DisableAnimationsTask::class.java) {
                    it.bridge = bridge
                }

                target.tasks.create("connectedAnimationsEnable", EnableAnimationsTask::class.java) {
                    it.bridge = bridge
                }

                target.tasks.create("connectedStayAwakeEnable", EnableStayAwakeTask::class.java) {
                    it.bridge = bridge
                }

                target.tasks.create("connectedStayAwakeDisable", DisableStayAwakeTask::class.java) {
                    it.bridge = bridge
                }

                target.tasks.create("connetedCheckWifi", CheckWifiTask::class.java) {
                    it.wifi = extension.wifi
                    it.bridge = bridge
                }
            }
        }
    }
}
