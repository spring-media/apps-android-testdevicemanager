package tasks

import com.android.ddmlib.IDevice
import internal.DeviceWrapper
import org.gradle.api.GradleException
import tasks.internal.LanguageCheckException
import tasks.internal.LanguageTask


open class SetLanguageTask : LanguageTask() {

    init {
        description = "check if specific locale is set. if not tries to set it using the external app"
    }

    override fun runTask2(device: IDevice) {
        try {
            super.runTask2(device)
        }
        catch (e: LanguageCheckException) {
            val deviceWrapper = DeviceWrapper(device, outputReceiverProvider)
            println("Device ${deviceWrapper.getDetails()} setting language to $language.")
            deviceWrapper.setLanguage(language)

            Thread.sleep(1000) //external app changes language via UI, so we need to wait for it

            val languageAfterChange = deviceWrapper.getLanguage()
            if (languageAfterChange != language) {
                throw GradleException("Different language set on ${deviceWrapper.getDetails()}. " +
                        "It's $languageAfterChange, while $language was expected.")
            }
            println("Device ${deviceWrapper.getDetails()} has locale set to $language.")
        }
    }
}