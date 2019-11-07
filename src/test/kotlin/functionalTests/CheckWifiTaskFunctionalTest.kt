package functionalTests

import BaseTest
import com.winterbe.expekt.should
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test

class CheckWifiTaskFunctionalTest : BaseTest("connectedCheckWifi", "checkWifi.gradle")