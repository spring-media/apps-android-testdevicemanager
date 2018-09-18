package tasks

import ShellCommands.INPUT_PRESS_ENTER
import ShellCommands.INPUT_PRESS_POWER_BUTTON
import ShellCommands.INPUT_TEXT
import ShellCommands.INPUT_WAKE_UP_CALL
import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import devicesCanBeFound
import executeShellCommandWithOutput
import getDeviceScreenResolution
import getSdkVersion
import isDeviceUnlocked
import isDisplayOn
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class UnlockDeviceTask : DefaultTask() {

    init {
        group = "deviceSetup"
        description = "unlock the device"
    }

    companion object {
        const val MINIMUM_DIGITS = 4
    }

    lateinit var device: IDevice

    @Input
    lateinit var android: AppExtension

    @Input
    lateinit var unlockBy: String

    @Input
    lateinit var pin: String

    @Input
    lateinit var password: String

    @Input
    lateinit var bridge: AndroidDebugBridge

    @TaskAction
    fun unlock() {
        devicesCanBeFound(bridge)

        bridge.devices.forEach { device ->

            this.device = device

            activateDisplay()

            when (unlockBy) {
                "power button" -> {
                }
                "swipe"        -> unlockBySwipe()
                "pin"          -> {
                    validatePin(pin)
                    unlockBySwipe()
                    unlockBy(pin)
                }
                "password"     -> {
                    validatePassword(password)
                    unlockBySwipe()
                    unlockBy(password)
                }
            }

            println("Screen of device ${device.getProperty("ro.product.model")} - " +
                            "Android ${device.getProperty("ro.build.version.release")} " +
                            "(API level: ${device.getProperty("ro.build.version.sdk")}) should be activated & unlocked.")
        }
    }

    private fun activateDisplay() {
        val sdkVersion = getSdkVersion(device)

        println("sdkVersion: $sdkVersion")
        if (sdkVersion < 20) {
            if (!isDisplayOn(device)) {
                println("activating Display")
                device.executeShellCommandWithOutput(INPUT_PRESS_POWER_BUTTON)
            }
        } else {
            println("activating Display")
            device.executeShellCommandWithOutput(INPUT_WAKE_UP_CALL)
        }
    }

    private fun unlockBySwipe() {
        val screenWidth = getDeviceScreenResolution(device).xCoordinate
        val screenHeight = getDeviceScreenResolution(device).yCoordinate
        val inputSwipeToUnlock = "input swipe ${screenWidth / 2} ${screenHeight - (screenHeight / 5)} " +
                "${screenWidth - (screenWidth / 5)} ${screenHeight / 5}"

        println("execute SWIPE_TO_UNLOCK: input swipe $inputSwipeToUnlock")

        device.executeShellCommandWithOutput(inputSwipeToUnlock)
    }

    private fun unlockBy(passPhrase: String) {
        if (!isDisplayOn(device)) activateDisplay()

        if (!isDeviceUnlocked(device)) {
            device.executeShellCommandWithOutput("$INPUT_TEXT $passPhrase")
            device.executeShellCommandWithOutput(INPUT_PRESS_ENTER)
        }
    }

    private fun validatePin(pin: String) {
        if (pin.isBlank()) {
            throw GradleException("Pin maintained to unlock the device is blank.")
        }

        pin.forEach {
            if (!it.isDigit()) {
                throw GradleException("Part of the pin maintained for unlocking the device is no number: $it .")
            }
        }

        val length = pin.length
        if (length < MINIMUM_DIGITS) {
            throw GradleException("Pin maintained for unlocking the device contains only $length digits. 4 digits is the minimum.")
        }
    }

    private fun validatePassword(password: String) {
        if (password.isBlank()) {
            throw GradleException("Password to unlock the device is blank.")
        }
    }
}
