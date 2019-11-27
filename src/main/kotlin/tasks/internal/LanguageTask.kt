package tasks.internal

import com.android.ddmlib.IDevice
import internal.DeviceWrapper
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input

open class LanguageTask : DefaultPluginTask() {
    @Input
    lateinit var language: String

    override fun runTask1() {
        if (language.isBlank()) {
            throw GradleException("No language provided in build script.")
        }
    }

    override fun runTask2(device: IDevice) {
        val deviceWrapper = DeviceWrapper(device, outputReceiverProvider)
        val currentLanguage = deviceWrapper.getLanguage()
        if (currentLanguage != language) {
            throw LanguageCheckException("Different language set on ${deviceWrapper.getDetails()}. " +
                    "It's $currentLanguage, while $language was expected.")
        }
        println("Device ${deviceWrapper.getDetails()} has locale set to $currentLanguage.")
    }

    override fun runTask3() {}
}

class LanguageCheckException(msg: String) : GradleException(msg)