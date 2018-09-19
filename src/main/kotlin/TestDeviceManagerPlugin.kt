import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import tasks.LockDeviceTask
import tasks.UnlockDeviceTask

class TestDeviceManagerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("deviceUnlocker", DeviceUnlockerExtension::class.java)

        target.afterEvaluate { project ->
            project.allprojects { subProject ->
                val android = subProject.extensions.getByType(AppExtension::class.java)
                val bridge = createAndroidDebugBridge(android)

                target.tasks.create("connectedUnlockDevice", UnlockDeviceTask::class.java) {
                    it.android = android
                    it.bridge = bridge
                    it.unlockBy = extension.unlockBy
                    it.pin = extension.pin
                    it.password = extension.password
                }

                target.tasks.create("connectedLockDevice", LockDeviceTask::class.java) {
                    it.android = android
                    it.bridge = bridge
                }
            }
        }

    }
}
