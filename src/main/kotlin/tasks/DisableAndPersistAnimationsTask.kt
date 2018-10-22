package tasks

import tasks.internal.SetAnimationsTask

open class DisableAndPersistAnimationsTask : SetAnimationsTask(enableAnimations = false) {

    init {
        description = "Disables animations for connected devices."
    }
}