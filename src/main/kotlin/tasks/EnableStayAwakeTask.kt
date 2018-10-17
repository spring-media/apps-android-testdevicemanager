package tasks

import internal.StayAwakeStatus.STAY_AWAKE
import tasks.internal.SetStayAwakeStatusTask

open class EnableStayAwakeTask : SetStayAwakeStatusTask(STAY_AWAKE) {

    init {
        description = "Activate the Stay Awake settings in the developer options."
    }
}