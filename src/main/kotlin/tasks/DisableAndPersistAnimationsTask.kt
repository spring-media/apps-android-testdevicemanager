package tasks

import internal.SetAnimationsStatus.DISABLE_ANIMATIONS
import tasks.internal.SetAnimationsTask

open class DisableAndPersistAnimationsTask : SetAnimationsTask(DISABLE_ANIMATIONS) {

    init {
        description = "Disables animations for connected devices."
    }
}