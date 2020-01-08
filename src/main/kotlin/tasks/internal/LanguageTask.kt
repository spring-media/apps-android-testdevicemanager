package tasks.internal

import com.android.ddmlib.IDevice
import internal.DeviceWrapper
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input

open class LanguageTask : DefaultPluginTask() {
    protected lateinit var deviceWrapper: DeviceWrapper
    protected lateinit var currentLanguage: String

    @Input
    lateinit var language: String

    override fun runTask1() {
        if (language.isBlank()) {
            throw GradleException("No language provided in build script.")
        }
    }

    override fun runTask2(device: IDevice) {
        hasLocaleDifferentThanExpected(device)
    }

    protected fun hasLocaleDifferentThanExpected(device: IDevice) :Boolean {
        deviceWrapper = DeviceWrapper(device, outputReceiverProvider)
        currentLanguage = deviceWrapper.getLanguage()

        return currentLanguage != language
    }

    override fun runTask3() {}
}

class LanguageCheckException(msg: String) : GradleException(msg)