package tasks

import internal.LockStatus.LOCK_DEVICE
import tasks.internal.SetLockStatusTask


open class LockDeviceTask : SetLockStatusTask(LOCK_DEVICE) {

    init {
        description = "lock the device"
    }
}