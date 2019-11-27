package tasks

import tasks.internal.LanguageTask


open class CheckLanguageTask : LanguageTask() {

    init {
        description = "check if specific locale is set as current language"
    }

}