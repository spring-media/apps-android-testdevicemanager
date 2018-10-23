package internal

import org.gradle.api.GradleException
import java.util.regex.Matcher
import java.util.regex.Pattern

class DataParser {

    fun getAnimationScalesFrom(configFileEntry: String): LinkedHashMap<String, Float> {
        val floatRegex = "(\\d+.\\d+)"
        val values = configFileEntry.analyzeByRegex(".+ $floatRegex $floatRegex $floatRegex")
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
        throw GradleException("The information you were looking for could not be found by regex: $regex " +
                                      "in the String $this.")
    }
    return stringMatcher
}