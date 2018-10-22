package tasks

import tasks.internal.SetLockStatusTask


open class LockDeviceTask : SetLockStatusTask(lockDevice = true) {

    init {
        description = "lock the device"
    }
}