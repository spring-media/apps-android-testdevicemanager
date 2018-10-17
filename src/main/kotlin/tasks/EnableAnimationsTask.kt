package tasks

import internal.SetAnimationsStatus.ENABLE_ANIMATIONS
import tasks.internal.SetAnimationsTask

open class EnableAnimationsTask : SetAnimationsTask(ENABLE_ANIMATIONS) {

    init {
        description = "Enables animations for connected devices."
    }
}