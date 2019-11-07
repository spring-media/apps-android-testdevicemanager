package internal

import org.gradle.api.GradleException

object ShellCommands {
    const val DUMPSYS_INPUT_METHOD = "dumpsys input_method"
    const val DUMPSYS_WINDOW = "dumpsys window"
    const val DUMPSYS_WIFI = "dumpsys wifi"
    const val INPUT_WAKE_UP_CALL = "input keyevent 224"
    const val INPUT_SLEEP_CALL = "input keyevent 223"
    const val INPUT_PRESS_POWER_BUTTON = "input keyevent 26"
    const val INPUT_PRESS_ENTER = "input keyevent 66"
    const val INPUT_TEXT = "input text"
    const val SETTINGS_GET_ANDROID_ID = "settings get secure android_id"
    const val SETTINGS_PUT_STAY_ON = "settings put global stay_on_while_plugged_in"
    const val SETTINGS_GET_STAY_ON = "settings get global stay_on_while_plugged_in"
    const val SETTINGS_PUT_GLOBAL = "settings put global"
    const val SETTINGS_GET_GLOBAL = "settings get global"
}

object TaskInfo {
    const val GROUP_NAME = "device setup"
}

object ShellOutput {
    const val noDeviceError = "error: no devices/emulators found"
}

object GradleException {
    const val noDevicesConnected = "No devices connected."
}

class NoDevicesConnectedException(
        override val message: String? = "No devices connected."
): GradleException()