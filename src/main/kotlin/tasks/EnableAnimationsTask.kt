package tasks

import tasks.internal.SetAnimationsTask

open class EnableAnimationsTask : SetAnimationsTask(enableAnimations = true) {

    init {
        description = "Enables animations for connected devices."
    }
}