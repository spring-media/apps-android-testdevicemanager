package tasks

import com.android.ddmlib.IDevice
import tasks.internal.LanguageCheckException
import tasks.internal.LanguageTask


open class CheckLanguageTask : LanguageTask() {

    init {
        description = "check if specific locale is set as current language"
    }

    override fun runTask2(device: IDevice) {
        if (hasLocaleDifferentThanExpected(device)) {
            throw LanguageCheckException("Different language set on ${deviceWrapper.getDetails()}. " +
                    "It's $currentLanguage, while $language was expected.")
        }
        println("Device ${deviceWrapper.getDetails()} has locale set to $currentLanguage.")
    }

}