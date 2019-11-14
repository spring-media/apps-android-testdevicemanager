package unitTest.internal

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import com.winterbe.expekt.should
import internal.AnimationScalesPersistenceHelper
import internal.DataParser
import org.gradle.api.Project
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AnimationScalesPersistenceHelperTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    private val dataParser: DataParser = mock()
    private val project: Project = mock()

    private val androidId = "androidId"
    private val androidId2 = "androidId2"
    private val androidAnimationScaleValues = linkedMapOf(
            "animation1" to 1.0F,
            "animation2" to 2.0F
    )
    private val configFileString = "androidId 1.0 2.0 "

    private val classToTest = AnimationScalesPersistenceHelper(
            project,
            dataParser
    )

    @Before
    fun setup() {
        val persistence = createPersistence()

        given(project.buildDir).willReturn(persistence.first)
        classToTest.createConfigFileInPath()
        classToTest.appendTextToConfigFileForId(androidId, androidAnimationScaleValues)
    }

    @Test
    fun `can get values from dataParser`() {
        classToTest.getValuesForDevice(androidId)

        then(dataParser).should().getAnimationScalesFrom(configFileString)
    }

    @Test
    fun `file has one entry for id and returns true`() {
        val result = classToTest.hasOneEntryForId(androidId)

        result.should.be.equal(true)
    }

    @Test
    fun `file has no entry for id and returns false`() {
        val result = classToTest.hasOneEntryForId(androidId2)

        result.should.be.equal(false)
    }

    @Test
    fun `text can be append to config file`() {
        classToTest.appendTextToConfigFileForId(androidId2, androidAnimationScaleValues)

        val result = classToTest.hasOneEntryForId(androidId2)

        result.should.be.`true`
    }

    @Test
    fun `entry of config file can be deleted`() {
        classToTest.deleteEntryForId(androidId)

        val result = classToTest.hasOneEntryForId(androidId)

        result.should.be.`false`
    }

    @Test
    fun `can check if config file exists`() {
        val result = classToTest.hasConfigFile()

        result.should.be.`true`
    }

    @Test
    fun `can delete config file`() {
        classToTest.deleteConfigFile()

        val result = classToTest.hasConfigFile()

        result.should.be.`false`
    }

    private fun createPersistence(): Pair<File, File> {
        val realOutputDirectory = temporaryFolder.newFolder("outDir")
        val realConfigFile = temporaryFolder.newFile("configFile.txt")
        realConfigFile.appendText(configFileString)

        return Pair(realOutputDirectory, realConfigFile)
    }
}
