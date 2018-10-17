import com.winterbe.expekt.should
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test

abstract class BaseTest(private val taskName: String, private val gradleFile: String = "noPluginInformation.gradle") {

    @get:Rule
    open val temporaryFolder = TemporaryTestFolder()

    @Test
    fun `fail when no device is connected`() {
        val exceptionMessage = "No devices connected."

        temporaryFolder.copyResourceToFile("base.gradle", "base.gradle")
        temporaryFolder.copyResourceToFile(gradleFile, "build.gradle")

        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withPluginClasspath()
                .withArguments(taskName)
                .buildAndFail()

        result.output.should.contain(exceptionMessage)
    }
}