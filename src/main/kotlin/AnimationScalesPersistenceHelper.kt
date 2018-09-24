import java.io.File

class AnimationScalesPersistenceHelper(private val outDir: File, private val configFile: File) {

    fun createOutputDirectory() {
        outDir.mkdir()
        println("Testdevicemanager directory created.")
    }

    fun createConfigFile() {
        configFile.createNewFile()
        println("Config file will be created.")
    }

    fun getValuesForDevice(androidId: String): AnimationsScales {
        val entry = configFile.readLines().filter { it.contains(androidId) }
        return getValuesFromString(entry[0])
    }

    private fun getValuesFromString(string: String): AnimationsScales {
        val values = string.analyzeByRegex(".+ (\\d+.\\d+) (\\d+.\\d+) (\\d+.\\d+)")
        return AnimationsScales(
                windowAnimation = values.group(1).toFloat(),
                transitionAnimation = values.group(2).toFloat(),
                animatorDuration = values.group(3).toFloat()
        )
    }

    fun hasOneEntryForId(androidId: String) =
            configFile.readLines().any { it.contains(androidId) } &&
                    configFile.readLines().filter { it.contains(androidId) }.size == 1

    fun appendTextToConfigFileForId(androidId: String, animationScaleValues: AnimationsScales): File {
        val configEntry = "$androidId ${animationScaleValues.windowAnimation} " +
                "${animationScaleValues.transitionAnimation} " +
                "${animationScaleValues.animatorDuration} "
        configFile.appendText("$configEntry \n")
        return configFile
    }

    fun deleteEntryForId(androidId: String): File {
        val lines = configFile.readLines()
        if (lines.isNotEmpty()) {
            var newString = ""
            lines.forEach { line ->
                if (!line.contains(androidId)) {
                    newString = "$newString$line \n"
                }
            }
            configFile.writeText(newString)
        }
        return configFile
    }
}
