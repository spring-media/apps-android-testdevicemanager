package tasks

import com.android.ddmlib.IDevice
import internal.DeviceWrapper
import org.gradle.api.tasks.Input
import tasks.internal.SetLockStatusTask
import tasks.internal.Unlocker

open class UnlockDeviceTask : SetLockStatusTask(lockDevice = false) {

    init {
        description = "unlock the device"
    }

    companion object {
        const val MINIMUM_DIGITS = 4
        const val ANDROID_API_LEVEL_44W = 20
    }

    @Input
    lateinit var unlockBy: String

    @Input
    lateinit var pin: String

    @Input
    lateinit var password: String

    override fun runTask2(device: IDevice) {
        super.runTask2(device)

        val deviceWrapper = DeviceWrapper(device, outputReceiverProvider)
        val unlocker = Unlocker(deviceWrapper, unlockBy, pin, password)
        unlocker.unlock()
    }
}
