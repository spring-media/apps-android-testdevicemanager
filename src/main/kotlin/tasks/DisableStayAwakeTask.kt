package tasks

import internal.StayAwakeStatus.STAY_NOT_AWAKE
import tasks.internal.SetStayAwakeStatusTask

open class DisableStayAwakeTask : SetStayAwakeStatusTask(STAY_NOT_AWAKE) {

    init {
        description = "Deactivate the Stay Awake settings in the developer options."
    }
}