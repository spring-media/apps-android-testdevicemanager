package tasks

import com.android.ddmlib.IDevice
import org.gradle.api.GradleException
import tasks.internal.LanguageTask

open class SetLanguageTask : LanguageTask() {

    init {
        description = "check if specific locale is set. if not tries to set it using the external app"
    }

    override fun runTask2(device: IDevice) {
        if (!hasLocaleDifferentThanExpected(device)) return

        val deviceDetails = deviceWrapper.getDetails()
        println("Device $deviceDetails setting language to $language.")
        deviceWrapper.setLanguage(language)

        Thread.sleep(1000) //external app changes language via UI, so we need to wait for it

        val languageAfterChange = deviceWrapper.getLanguage()
        if (languageAfterChange != language) {
            throw GradleException("Different language set on $deviceDetails. " +
                    "It's $languageAfterChange, while $language was expected.")
        }
        println("Device $deviceDetails has locale set to $language.")
    }
}