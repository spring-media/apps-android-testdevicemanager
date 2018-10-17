package functionalTests

import BaseTest
import com.winterbe.expekt.should
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test

class CheckWifiTaskFunctionalTest : BaseTest("connectedCheckWifi", "checkWifi.gradle") {

    @Test
    fun `fail when no wifi information is maintained in build script`() {
        val exceptionMessage = "No name for wifi maintained in build script."

        temporaryFolder.copyResourceToFile("base.gradle", "base.gradle")
        temporaryFolder.copyResourceToFile("noPluginInformation.gradle", "build.gradle")

        val result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withPluginClasspath()
                .withArguments("connectedCheckWifi")
                .buildAndFail()

        result.output.should.contain(exceptionMessage)
    }
}