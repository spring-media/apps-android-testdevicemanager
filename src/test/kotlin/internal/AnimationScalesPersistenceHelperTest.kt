package internal

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import com.winterbe.expekt.should
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AnimationScalesPersistenceHelperTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    private val outDir: File = mock()
    private val configFile: File = mock()
    private val dataParser: DataParser = mock()

    private val androidId = "androidId"
    private val androidId2 = "androidId2"
    private val configFileString = "androidId 1.0 1.0 1.0"
    private val configFileString2 = "androidId2 1.0 1.0 1.0"
    private val animationsScales = createAnimationsScalesWithValue(1F)

    private val classToTest = AnimationScalesPersistenceHelper(
            outDir,
            configFile,
            dataParser
    )

    @Test
    fun `can create output directory`() {
        classToTest.createOutputDirectory()

        then(outDir).should().mkdir()
    }

    @Test
    fun `can create config file`() {
        classToTest.createConfigFile()

        then(configFile).should().createNewFile()
    }

    @Test
    fun `can get values from dataParser`() {
        val classToTest = createPersistenceHelperWithRealFiles()
        classToTest.getValuesForDevice(androidId)

        then(dataParser).should().getAnimationScalesFrom(configFileString)
    }

    @Test
    fun `check if file has one entry for id can be true`() {
        val classToTest = createPersistenceHelperWithRealFiles()
        val result = classToTest.hasOneEntryForId(androidId)

        result.should.be.equal(true)
    }

    @Test
    fun `check if file has one entry for id can be false`() {
        val classToTest = createPersistenceHelperWithRealFiles()
        val result = classToTest.hasOneEntryForId(androidId2)

        result.should.be.equal(false)
    }

    @Test
    fun `text can be append to config file`() {
        val classToTest = createPersistenceHelperWithRealFiles()
        val file = classToTest.appendTextToConfigFileForId(androidId2, animationsScales)
        val result = file.readText()

        result.should.contain(configFileString2)
    }

    @Test
    fun `entry of config file can be deleted`() {
        val classToTest = createPersistenceHelperWithRealFiles()

        val file = classToTest.deleteEntryForId(androidId)
        val result = file.readText()

        result.should.be.empty
    }

    @Test
    fun `can check if output directory exists`() {
        classToTest.hasOutputDir()

        then(outDir).should().exists()
    }

    @Test
    fun `can check if config file exists`() {
        classToTest.hasConfigFile()

        then(configFile).should().exists()
    }

    @Test
    fun `can delete output directory`() {
        classToTest.deleteOutputDir()

        then(outDir).should().delete()
    }

    @Test
    fun `can delete config file`() {
        classToTest.deleteConfigFile()

        then(configFile).should().delete()
    }

    private fun createPersistenceHelperWithRealFiles(): AnimationScalesPersistenceHelper {
        val persistence = createPersistence()
        return AnimationScalesPersistenceHelper(persistence.first, persistence.second, dataParser)
    }

    private fun createPersistence(): Pair<File, File> {
        val realOutputDirectory = temporaryFolder.newFolder("outDir")
        val realConfigFile = temporaryFolder.newFile("configFile.txt")
        realConfigFile.appendText(configFileString)

        return Pair(realOutputDirectory, realConfigFile)
    }
}
