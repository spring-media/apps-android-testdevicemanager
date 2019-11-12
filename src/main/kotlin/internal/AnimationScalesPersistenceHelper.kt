package internal

import TestDeviceManagerPlugin.Companion.CONFIG_FILE_PATH
import org.gradle.api.Project
import java.io.File

class AnimationScalesPersistenceHelper(
        private val project: Project,
        private val dataParser: DataParser
) {

    private val configFile: File by lazy {
        File(project.buildDir, CONFIG_FILE_PATH)
    }

    fun createConfigFileInPath() {
        if (configFile.parentFile.mkdirs()) {
            println("/${configFile.parentFile.name} directory created...")
            configFile.createNewFile()
            println("${configFile.name} created.")
        }
    }

    fun getValuesForDevice(androidId: String): LinkedHashMap<String, Float> {
        val configFileEntry = configFile.filterLines { it.contains(androidId) }
        return dataParser.getAnimationScalesFrom(configFileEntry[0])
    }

    fun hasOneEntryForId(androidId: String): Boolean {
        return readConfigFileLines()?.let { lines ->
            (lines.any { it.contains(androidId) } &&
                    configFile.filterLines { it.contains(androidId) }.size == 1)
        } ?: false
    }

    fun appendTextToConfigFileForId(androidId: String, animationScaleValues: LinkedHashMap<String, Float>) {
        val stringBuilder = StringBuilder("$androidId ")

        animationScaleValues.forEach {
            stringBuilder.append("${it.value} ")
        }

        stringBuilder.append("\n")

        val configEntry = stringBuilder.toString()
        configFile.appendText(configEntry)
    }

    fun deleteEntryForId(androidId: String) {
        readConfigFileLines()?.let { lines ->
            if (!lines.isNotEmpty()) return

            val stringBuilder = StringBuilder()
            lines.forEach { line ->
                if (!line.contains(androidId)) {
                    stringBuilder.append("$line\n")
                }
            }
            val newConfigFile = stringBuilder.toString()
            configFile.writeText(newConfigFile)
        }
    }

    fun hasConfigFile() = configFile.exists()

    fun deleteConfigFile() {
        if (hasConfigFile()) {
            val path = configFile.parentFile
            configFile.delete()
            path.delete()
        }
    }

    private fun readConfigFileLines(): List<String>? {
        return if (configFile.exists()) {
            configFile.readLines()
        } else null
    }

    private fun File.filterLines(predicate: (String) -> Boolean): List<String> {
        return this.readLines().filter(predicate)
    }
}
