import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import tasks.DisableAnimationsTask
import tasks.EnableAnimationsTask
import tasks.LockDeviceTask
import tasks.UnlockDeviceTask

class TestDeviceManagerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("deviceUnlocker", DeviceUnlockerExtension::class.java)

        target.afterEvaluate { project ->
            project.allprojects { subProject ->
                val androidAppExtension = subProject.extensions.getByType(AppExtension::class.java)
                val bridge = androidAppExtension.createAndroidDebugBridge()

                target.tasks.create("connectedUnlockDevice", UnlockDeviceTask::class.java) {
                    it.android = androidAppExtension
                    it.bridge = bridge
                    it.unlockBy = extension.unlockBy
                    it.pin = extension.pin
                    it.password = extension.password
                }

                target.tasks.create("connectedLockDevice", LockDeviceTask::class.java) {
                    it.android = androidAppExtension
                    it.bridge = bridge
                }

                target.tasks.create("connectedDisableAnimations", DisableAnimationsTask::class.java) {
                    it.disableAnimations = extension.disableAnimations
                    it.bridge = bridge
                }

                target.tasks.create("connectedEnableAnimations", EnableAnimationsTask::class.java) {
                    it.enableAnimations = extension.enableAnimations
                    it.bridge = bridge
                }
            }
        }
    }
}
