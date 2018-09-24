import ShellCommands.DUMPSYS_INPUT_METHOD


import ShellCommands.DUMPSYS_WIFI
import ShellCommands.DUMPSYS_WINDOW
import ShellCommands.GETPROP_DEVICE_SDK_VERSION
import ShellCommands.SETTINGS_GET_ANDROID_ID
import ShellCommands.SETTINGS_GET_GLOBAL
import ShellCommands.SETTINGS_GET_STAY_ON
import ShellCommands.SETTINGS_PUT_GLOBAL
import ShellCommands.SETTINGS_PUT_STAY_ON
import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
import org.gradle.api.GradleException
import java.util.regex.Matcher
import java.util.regex.Pattern


fun AndroidDebugBridge.devicesCanBeFound() {
    if (this.devices.isEmpty()) {
        throw GradleException("No devices connected.")
    }
}

fun AnimationsScales.areAllZero(): Boolean {
    return this.windowAnimation == 0F &&
            this.transitionAnimation == 0F &&
            this.animatorDuration == 0F
}

fun AnimationsScales.hasNoZeros(): Boolean {
    return this.windowAnimation != 0F &&
            this.transitionAnimation != 0F &&
            this.animatorDuration != 0F
}

fun AppExtension.createAndroidDebugBridge(): AndroidDebugBridge {
    AndroidDebugBridge.initIfNeeded(false)
    return AndroidDebugBridge.createBridge(this.adbExecutable.path, false)
            ?: throw GradleException("ADB could not be created.")
}

fun IDevice.checkWifi(wifi: String) {
    val output = this.analyzeOutputOfShellCommandByRegex(DUMPSYS_WIFI, "mNetworkInfo .+ extra: \"(.+)\"")
    val currentWifi = output.group(1)
    if (currentWifi != wifi) {
        throw GradleException("Device ${this.details()} is not connected to wifi with name $wifi")
    }
}

fun IDevice.details(): String {
    return "${this.getProperty("ro.product.model")} " +
            "- Android ${this.getProperty("ro.build.version.release")} " +
            "(API level: ${this.getProperty("ro.build.version.sdk")})"
}

fun IDevice.executeShellCommandWithOutput(shellCommand: String): String {
    val outputReceiver = CollectingOutputReceiver()
    this.executeShellCommand(shellCommand, outputReceiver)
    return outputReceiver.output
}

fun IDevice.isDeviceUnlocked(): Boolean {
    val output = this.analyzeOutputOfShellCommandByRegex(DUMPSYS_WINDOW, "mFocusedWindow=Window\\{\\w+ u0 (\\w+)")
    return !screenNames.contains(output.group(1).trim())
}

fun IDevice.isDisplayOn(): Boolean {
    val output = this.executeShellCommandWithOutput(DUMPSYS_INPUT_METHOD)
    return output.contains("mScreenOn=true")
}

fun IDevice.getAndroidId(): String {
    val output = this.executeShellCommandWithOutput(SETTINGS_GET_ANDROID_ID)
    return output.trim()
}

fun IDevice.getAnimationValues(): AnimationsScales {
    return AnimationsScales(
            windowAnimation = this.getAnimationValue(animationScales[0]),
            transitionAnimation = this.getAnimationValue(animationScales[1]),
            animatorDuration = this.getAnimationValue(animationScales[2])
    )
}

fun IDevice.getDeviceScreenResolution(): ScreenResolution {
    val output = this.analyzeOutputOfShellCommandByRegex(DUMPSYS_WINDOW, "mUnrestrictedScreen.*?(\\d+)x(\\d+)")
    val screenWidth = output.group(1).trim().toInt()
    val screenHeight = output.group(2).trim().toInt()
    return ScreenResolution(xCoordinate = screenWidth, yCoordinate = screenHeight)
}

fun IDevice.getSdkVersion(): Int {
    val output = this.executeShellCommandWithOutput(GETPROP_DEVICE_SDK_VERSION)
    return output.trim().toInt()
}

fun IDevice.printAnimationValues() {
    this.printAnimationValue(animationScales[0])
    this.printAnimationValue(animationScales[1])
    this.printAnimationValue(animationScales[2])
    println("\n")
}

fun IDevice.setAnimationValues(scales: AnimationsScales) {
    this.setAnimationValue(animationScales[0], scales.windowAnimation)
    this.setAnimationValue(animationScales[1], scales.transitionAnimation)
    this.setAnimationValue(animationScales[2], scales.animatorDuration)
}

fun IDevice.getStayAwakeStatus(): Int {
    val output = this.executeShellCommandWithOutput(SETTINGS_GET_STAY_ON)
    return output.trim().toInt()
}

fun IDevice.setStayAwakeStatus(status: Boolean) {
    if (status) {
        this.executeShellCommandWithOutput("$SETTINGS_PUT_STAY_ON 2")
    } else {
        this.executeShellCommandWithOutput("$SETTINGS_PUT_STAY_ON 0")
    }
}

fun String.analyzeByRegex(regex: String): Matcher {
    val pattern = Pattern.compile(regex)
    val newString = pattern.matcher(this)
    if (!newString.find()) {
        throw GradleException("The information you were looking for could not be found by regex: $regex .")
    }
    return newString
}

private fun IDevice.analyzeOutputOfShellCommandByRegex(shellCommand: String, regex: String): Matcher {
    val output = this.executeShellCommandWithOutput(shellCommand)
    return output.analyzeByRegex(regex)
}

private fun IDevice.printAnimationValue(animation: String) {
    val output = this.getAnimationValue(animation)
    println("$animation for ${this.details()} is $output now.")
}

private fun IDevice.getAnimationValue(animation: String): Float {
    val output = this.executeShellCommandWithOutput("$SETTINGS_GET_GLOBAL ${animation}_scale")
    return output.trim().toFloat()
}

private fun IDevice.setAnimationValue(animation: String, value: Float) {
    this.executeShellCommandWithOutput("$SETTINGS_PUT_GLOBAL ${animation}_scale $value")
}
