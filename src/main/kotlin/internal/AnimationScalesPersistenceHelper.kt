package internal

import java.io.File

class AnimationScalesPersistenceHelper(
        private val outDir: File,
        private val configFile: File,
        private val dataParser: DataParser
) {

    fun createOutputDirectory() {
        outDir.mkdir()
        println("/${outDir.name} directory created.")
    }

    fun createConfigFile() {
        configFile.createNewFile()
        println("Config file will be created.")
    }

    fun getValuesForDevice(androidId: String): HashMap<String, Float> {
        val configFileEntry = configFile.filterLines { it.contains(androidId) }
        return dataParser.getAnimationScalesFrom(configFileEntry[0])
    }

    fun hasOneEntryForId(androidId: String): Boolean {
        val lines = readConfigFileLines()
        return (lines.any { it.contains(androidId) } &&
                configFile.filterLines { it.contains(androidId) }.size == 1)
    }

    fun appendTextToConfigFileForId(androidId: String, animationScaleValues: HashMap<String, Float>): File {
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            append(androidId)
            append(" ")
        }

        animationScaleValues.forEach {
            stringBuilder.apply {
                append(it.value)
                append(" ")
            }
        }

        stringBuilder.append("\n")
        val configEntry = stringBuilder.toString()
        configFile.appendText(configEntry)
        return configFile
    }

    fun deleteEntryForId(androidId: String): File {
        val currentConfigFile = readConfigFileLines()
        val stringBuilder = StringBuilder()

        if (currentConfigFile.isNotEmpty()) {
            currentConfigFile.forEach { line ->
                if (!line.contains(androidId)) {
                    stringBuilder.append(line).append("\n")
                }
            }
            val newConfigFile = stringBuilder.toString()
            configFile.writeText(newConfigFile)
        }
        return configFile
    }

    fun hasOutputDir() = outDir.exists()

    fun hasConfigFile() = configFile.exists()

    fun deleteConfigFile() = configFile.delete()

    fun deleteOutputDir() = outDir.delete()

    private fun readConfigFileLines(): List<String> {
        return configFile.readLines()
    }

    private fun File.filterLines(predicate: (String) -> Boolean): List<String> {
        return this.readLines().filter(predicate)
    }
}
