package functionalTests

import TemporaryTestFolder
import com.winterbe.expekt.should
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test

class TestDeviceManagerPluginFunctionalTest {

    @get:Rule
    val temporaryFolder = TemporaryTestFolder()

    @Test
    fun `project can build with plugin`() {
        temporaryFolder.copyResourceToFile("base.gradle", "base.gradle")
        temporaryFolder.copyResourceToFile("noPluginInformation.gradle", "build.gradle")

        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withPluginClasspath()
                .build()

        result.output.should.contain("BUILD SUCCESSFUL")
    }
}