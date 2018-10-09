package internal

import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
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

fun AnimationsScales.haveNoZeros(): Boolean {
    return this.windowAnimation != 0F &&
            this.transitionAnimation != 0F &&
            this.animatorDuration != 0F
}

fun AppExtension.createAndroidDebugBridge(): AndroidDebugBridge {
    AndroidDebugBridge.initIfNeeded(false)
    return AndroidDebugBridge.createBridge(this.adbExecutable.path, false)
            ?: throw GradleException("ADB could not be created.")
}

fun String.analyzeByRegex(regex: String): Matcher {
    val pattern = Pattern.compile(regex)
    val newString = pattern.matcher(this)
    if (!newString.find()) {
        throw GradleException("The information you were looking for could not be found by regex: $regex .")
    }
    return newString
}