import com.winterbe.expekt.should
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class TestDeviceManagerPluginFunctionalTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var buildGradle: File

    @Before
    fun setup() {
        //Prepare build gradle
        buildGradle = temporaryFolder.newFile("build.gradle")
    }

    @Test
    fun `first test`() {
        buildGradle.appendText("""
            plugins {
                id 'de.welt.apps.testdevicemanager'
                id 'com.android.application'
            }
        """.trimIndent())

        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withPluginClasspath()
                .build()

        result.output.should.contain("BUILD SUCCESSFUL")
    }


}