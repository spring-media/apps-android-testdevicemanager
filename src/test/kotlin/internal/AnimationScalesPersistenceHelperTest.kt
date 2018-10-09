package internal

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import com.winterbe.expekt.should
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.internal.verification.Times
import java.io.File

class AnimationScalesPersistenceHelperTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    private val outDir: File = mock()
    private val configFile: File = mock()

    private val androidId = "androidId"
    private val androidId2 = "androidId2"
    private val configFileString = "androidId 1.0 1.0 1.0"
    private val configFileString2 = "androidId2 1.0 1.0 1.0  \n"
    private val animationsScales = AnimationsScales(1F, 1F, 1F)

    private val classToTest = AnimationScalesPersistenceHelper(
            outDir,
            configFile
    )

    @Test
    fun `can create output directory`() {
        classToTest.createOutputDirectory()

        then(outDir).should(Times(1)).mkdir()
    }

    @Test
    fun `can create config file`() {
        classToTest.createConfigFile()

        then(configFile).should(Times(1)).createNewFile()
    }

    @Test
    fun `can get values from file for device`() {
        val persistence = createPersistence()

        val classToTest = AnimationScalesPersistenceHelper(persistence.first, persistence.second)
        val result = classToTest.getValuesForDevice(androidId)

        result.should.equal(animationsScales)
    }

    @Test
    fun `check if file has one entry for id can be true`() {
        val persistence = createPersistence()

        val classToTest = AnimationScalesPersistenceHelper(persistence.first, persistence.second)
        val result = classToTest.hasOneEntryForId(androidId)

        result.should.be.equal(true)
    }

    @Test
    fun `check if file has one entry for id can be false`() {
        val persistence = createPersistence()

        val classToTest = AnimationScalesPersistenceHelper(persistence.first, persistence.second)
        val result = classToTest.hasOneEntryForId(androidId2)

        result.should.be.equal(false)
    }

    @Test
    fun `text can be append to config file`() {
        val realOutputDirectory = temporaryFolder.newFolder("outDir")
        val realConfigFile = temporaryFolder.newFile("configFile.txt")

        val classToTest = AnimationScalesPersistenceHelper(realOutputDirectory, realConfigFile)
        val file = classToTest.appendTextToConfigFileForId(androidId2, animationsScales)
        val result = file.readText()

        result.should.equal(configFileString2)
    }

    @Test
    fun `entry of config file can be deleted`() {
        val persistence = createPersistence()

        val classToTest = AnimationScalesPersistenceHelper(persistence.first, persistence.second)
        val file = classToTest.deleteEntryForId(androidId)
        val result = file.readText()

        result.should.be.empty
    }

    @Test
    fun `can check if output directory exists`() {
        classToTest.hasOutputDir()

        then(outDir).should(Times(1)).exists()
    }

    @Test
    fun `can check if config file exists`() {
        classToTest.hasConfigFile()

        then(configFile).should(Times(1)).exists()
    }

    @Test
    fun `can delete output directory`() {
        classToTest.deleteOutputDir()

        then(outDir).should(Times(1)).delete()
    }

    @Test
    fun `can delete config file`() {
        classToTest.deleteConfigFile()

        then(configFile).should(Times(1)).delete()
    }


    private fun createPersistence(): Pair<File, File> {
        val realOutputDirectory = temporaryFolder.newFolder("outDir")
        val realConfigFile = temporaryFolder.newFile("configFile.txt")
        realConfigFile.appendText(configFileString)

        return Pair(realOutputDirectory, realConfigFile)
    }
}