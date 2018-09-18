import ShellCommands.DUMPSYS_INPUT_METHOD
import ShellCommands.DUMPSYS_WINDOW
import ShellCommands.GETPROP_DEVICE_SDK_VERSION
import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import org.gradle.api.GradleException
import java.util.regex.Matcher
import java.util.regex.Pattern

fun createAndroidDebugBridge(android: AppExtension): AndroidDebugBridge {
    AndroidDebugBridge.initIfNeeded(false)

    return AndroidDebugBridge.createBridge(android.adbExecutable.path, false)
            ?: throw GradleException("ADB could not be created.")
}

fun devicesCanBeFound(bridge: AndroidDebugBridge) {
    if (bridge.devices.isEmpty()) {
        throw GradleException("No devices connected.")
    }
}

fun getSdkVersion(device: IDevice): Int {
    val output = device.executeShellCommandWithOutput(GETPROP_DEVICE_SDK_VERSION)

    return output.trim().toInt()
}

fun isDisplayOn(device: IDevice): Boolean {
    val output = device.executeShellCommandWithOutput(DUMPSYS_INPUT_METHOD)

    return output.contains("mScreenOn=true")
}

fun getDeviceScreenResolution(device: IDevice): ScreenResolution {
    val output = device.analyzeOutputOfShellCommandByRegex(DUMPSYS_WINDOW, "mUnrestrictedScreen.*?(\\d+)x(\\d+)")

    val screenWidth = output.group(1).trim().toInt()
    val screenHeight = output.group(2).trim().toInt()

    return ScreenResolution(xCoordinate = screenWidth, yCoordinate = screenHeight)
}

fun isDeviceUnlocked(device: IDevice): Boolean {
    val screenNames = listOf("StatusBar", "Bouncer", "Keyguard")

    val output = device.analyzeOutputOfShellCommandByRegex(DUMPSYS_WINDOW, "mFocusedWindow=Window\\{\\w+ u0 (\\w+)")

    return !screenNames.contains(output.group(1).trim())
}

fun IDevice.analyzeOutputOfShellCommandByRegex(shellCommand: String, regex: String): Matcher {
    val output = this.executeShellCommandWithOutput(shellCommand)

    val pattern = Pattern.compile(regex)
    val information = pattern.matcher(output)

    if (!information.find()) {
        throw GradleException("The information you were looking for could not be gathered by shell command: " +
                                      "$shellCommand and regex: $regex .")
    }

    return information
}
