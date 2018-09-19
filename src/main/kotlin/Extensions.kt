import ShellCommands.DUMPSYS_INPUT_METHOD
import ShellCommands.DUMPSYS_WINDOW
import ShellCommands.GETPROP_DEVICE_SDK_VERSION
import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
import org.gradle.api.GradleException
import java.util.regex.Matcher
import java.util.regex.Pattern


fun IDevice.isDisplayOn(): Boolean {
    val output = this.executeShellCommandWithOutput(DUMPSYS_INPUT_METHOD)
    return output.contains("mScreenOn=true")
}

fun IDevice.getSdkVersion(): Int {
    val output = this.executeShellCommandWithOutput(GETPROP_DEVICE_SDK_VERSION)
    return output.trim().toInt()
}

fun IDevice.getDeviceScreenResolution(): ScreenResolution {
    val output = this.analyzeOutputOfShellCommandByRegex(DUMPSYS_WINDOW, "mUnrestrictedScreen.*?(\\d+)x(\\d+)")
    val screenWidth = output.group(1).trim().toInt()
    val screenHeight = output.group(2).trim().toInt()

    return ScreenResolution(xCoordinate = screenWidth, yCoordinate = screenHeight)
}

fun IDevice.isDeviceUnlocked(): Boolean {
    val screenNames = listOf("StatusBar", "Bouncer", "Keyguard")
    val output = this.analyzeOutputOfShellCommandByRegex(DUMPSYS_WINDOW, "mFocusedWindow=Window\\{\\w+ u0 (\\w+)")

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

fun IDevice.executeShellCommandWithOutput(shellCommand: String): String {
    val outputReceiver = CollectingOutputReceiver()

    this.executeShellCommand(shellCommand, outputReceiver)

    return outputReceiver.output
}

fun IDevice.setAnimationValue(animation: String, value: Int) {
    this.executeShellCommandWithOutput("settings put global ${animation}_scale $value")
}

fun IDevice.printAnimationValue(animation: String) {
    val output = this.getAnimationValue(animation)
    println("$animation for ${this.getProperty("ro.product.model")} is $output now.")
}

fun IDevice.getAnimationValue(animation: String): Int {
    val output = this.executeShellCommandWithOutput("settings get global ${animation}_scale")
    return output.trim().toInt()
}

fun AndroidDebugBridge.devicesCanBeFound() {
    if (this.devices.isEmpty()) {
        throw GradleException("No devices connected.")
    }
}

fun AppExtension.createAndroidDebugBridge(): AndroidDebugBridge {
    AndroidDebugBridge.initIfNeeded(false)

    return AndroidDebugBridge.createBridge(this.adbExecutable.path, false)
            ?: throw GradleException("ADB could not be created.")
}
