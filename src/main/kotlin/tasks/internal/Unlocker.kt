package tasks.internal

import internal.DeviceWrapper
import internal.ShellCommands.INPUT_PRESS_ENTER
import internal.ShellCommands.INPUT_TEXT
import internal.UnlockMethods.*
import org.gradle.api.GradleException
import tasks.UnlockDeviceTask.Companion.MINIMUM_DIGITS

class Unlocker(
        private val deviceWrapper: DeviceWrapper,
        private val unlockBy: String,
        private val pin: String,
        private val password: String
) {

    fun unlock() {
        when (unlockBy) {
            POWER_BUTTON.string -> {
            }
            SWIPE.string        -> unlockBySwipe()
            PIN.string          -> {
                validatePin(pin)
                unlockBySwipe()
                unlockBy(pin)
            }
            PASSWORD.string     -> {
                validatePassword(password)
                unlockBySwipe()
                unlockBy(password)
            }
            else                -> throw GradleException("The unlock method maintained in the build script is not available.")
        }
    }

    private fun unlockBySwipe() {
        val resolution = deviceWrapper.getDeviceScreenResolution()
        val screenWidth = resolution.xCoordinate
        val screenHeight = resolution.yCoordinate
        val inputSwipeToUnlock = "input swipe ${screenWidth / 2} ${screenHeight - (screenHeight / 5)} " +
                "${screenWidth - (screenWidth / 5)} ${screenHeight / 5}"

        deviceWrapper.executeShellCommandWithOutput(inputSwipeToUnlock)
    }

    private fun unlockBy(passPhrase: String) {
        if (!deviceWrapper.isDeviceUnlocked()) {
            deviceWrapper.executeShellCommandWithOutput("$INPUT_TEXT $passPhrase")
            deviceWrapper.executeShellCommandWithOutput(INPUT_PRESS_ENTER)
        }
    }

    private fun validatePin(pin: String) {
        if (pin.isBlank()) {
            throw GradleException("Pin to unlock the device is blank or not maintained in the build script.")
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
            throw GradleException("Password to unlock the device is blank or not maintained in build script.")
        }
    }

}
