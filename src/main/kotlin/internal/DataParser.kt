package internal

import org.gradle.api.GradleException
import java.util.regex.Matcher
import java.util.regex.Pattern

class DataParser {

    fun getAnimationScalesFrom(configFileEntry: String): HashMap<String, Float> {
        val values = configFileEntry.analyzeByRegex(".+ (\\d+.\\d+) (\\d+.\\d+) (\\d+.\\d+)")
        var index = 1

        animationScales.forEach {
            animationScales[it.key] = values.getFloatOf(index)
            index++
        }
        return animationScales
    }

    private fun Matcher.getFloatOf(index: Int) = this.group(index).toFloat()
}

fun String.analyzeByRegex(regex: String): Matcher {
    val pattern = Pattern.compile(regex)
    val stringMatcher = pattern.matcher(this)
    if (!stringMatcher.find()) {
        throw GradleException("The information you were looking for could not be found by regex: $regex .")
    }
    return stringMatcher
}