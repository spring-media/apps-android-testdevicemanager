package internal

import com.android.ddmlib.IDevice
import internal.ShellCommands.DUMPSYS_INPUT_METHOD
import internal.ShellCommands.DUMPSYS_WIFI
import internal.ShellCommands.DUMPSYS_WINDOW
import internal.ShellCommands.SETTINGS_GET_ANDROID_ID
import internal.ShellCommands.SETTINGS_GET_GLOBAL
import internal.ShellCommands.SETTINGS_GET_STAY_ON
import internal.ShellCommands.SETTINGS_PUT_GLOBAL
import internal.ShellCommands.SETTINGS_PUT_STAY_ON
import org.gradle.api.GradleException
import java.util.regex.Matcher

class DeviceWrapper(val device: IDevice, val outputReceiverProvider: OutputReceiverProvider) {

    fun isDisplayOn(): Boolean {
        val output = executeShellCommandWithOutput(DUMPSYS_INPUT_METHOD)
        return output.contains("mScreenOn=true")
    }

    fun getDetails(): String {
        return "${device.getProperty("ro.product.model")} " +
                "- Android ${device.getProperty("ro.build.version.release")} " +
                "(API level: ${device.getProperty("ro.build.version.sdk")})"
    }

    fun getDeviceScreenResolution(): ScreenResolution {
        val output = analyzeOutputOfShellCommandByRegex(DUMPSYS_WINDOW, "mUnrestrictedScreen.*?(\\d+)x(\\d+)")
        val screenWidth = output.group(1).trim().toInt()
        val screenHeight = output.group(2).trim().toInt()
        return ScreenResolution(xCoordinate = screenWidth, yCoordinate = screenHeight)
    }

    fun isDeviceUnlocked(): Boolean {
        val output = analyzeOutputOfShellCommandByRegex(DUMPSYS_WINDOW, "mFocusedWindow=Window\\{\\w+ u0 (\\w+)")
        return !screenNames.contains(output.group(1).trim())
    }

    fun checkWifi(wifi: String) {
        val output = analyzeOutputOfShellCommandByRegex(DUMPSYS_WIFI, "mNetworkInfo .+ extra: \"(.+)\"")
        val currentWifi = output.group(1)
        if (currentWifi != wifi) {
            throw GradleException("Device ${getDetails()} is not connected to wifi with name $wifi")
        }
    }

    fun getStayAwakeStatus(): Int {
        val output = executeShellCommandWithOutput(SETTINGS_GET_STAY_ON)
        return output.trim().toInt()
    }

    fun setStayAwakeStatus(status: StayAwakeStatus) {
        executeShellCommandWithOutput("$SETTINGS_PUT_STAY_ON ${status.value}")
    }

    fun getAndroidId(): String {
        val output = executeShellCommandWithOutput(SETTINGS_GET_ANDROID_ID)
        return output.trim()
    }

    fun getAnimationValues(): AnimationsScales {
        return AnimationsScales(
                windowAnimation = getAnimationValue(animationScalesNames[0]),
                transitionAnimation = getAnimationValue(animationScalesNames[1]),
                animatorDuration = getAnimationValue(animationScalesNames[2])
        )
    }

    fun setAnimationValues(scales: AnimationsScales) {
        setAnimationValue(animationScalesNames[0], scales.windowAnimation)
        setAnimationValue(animationScalesNames[1], scales.transitionAnimation)
        setAnimationValue(animationScalesNames[2], scales.animatorDuration)
    }

    fun printAnimationValues() {
        printAnimationValue(animationScalesNames[0])
        printAnimationValue(animationScalesNames[1])
        printAnimationValue(animationScalesNames[2])
        println("\n")
    }

    fun executeShellCommandWithOutput(shellCommand: String): String {
        val outputReceiver = outputReceiverProvider.get()
        device.executeShellCommand(shellCommand, outputReceiver)
        return outputReceiver.output
    }

    private fun analyzeOutputOfShellCommandByRegex(shellCommand: String, regex: String): Matcher {
        val output = executeShellCommandWithOutput(shellCommand)
        return output.analyzeByRegex(regex)
    }

    private fun setAnimationValue(animation: String, value: Float) {
        executeShellCommandWithOutput("$SETTINGS_PUT_GLOBAL ${animation}_scale $value")
    }

    private fun printAnimationValue(animation: String) {
        val output = getAnimationValue(animation)
        println("$animation for ${getDetails()} is $output now.")
    }

    private fun getAnimationValue(animation: String): Float {
        val output = executeShellCommandWithOutput("$SETTINGS_GET_GLOBAL ${animation}_scale")
        return output.trim().toFloat()
    }
}
