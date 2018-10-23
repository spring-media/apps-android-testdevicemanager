import com.android.build.gradle.AppExtension
import com.android.ddmlib.AndroidDebugBridge
import internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import tasks.internal.AnimationScalesSwitch
import java.io.File

class TestDeviceManagerPlugin : Plugin<Project> {

    companion object {
        const val EXTENSION_NAME = "testDeviceManager"
        const val OUTPUT_DIRECTORY_PATH = "/generated/source/testDeviceManager"
        const val CONFIG_FILE_PATH = "$OUTPUT_DIRECTORY_PATH/configFile.txt"
    }

    private lateinit var project: Project
    private lateinit var androidAppExtension: AppExtension
    private lateinit var bridge: AndroidDebugBridge
    private lateinit var extension: TestDeviceManagerExtension
    private lateinit var outDir: File
    private lateinit var configFile: File
    private lateinit var dataParser: DataParser
    private lateinit var communicator: DeviceCommunicator
    private lateinit var outputReceiverProvider: OutputReceiverProvider
    private lateinit var animationScalesPersistenceHelper: AnimationScalesPersistenceHelper
    private lateinit var animationScalesSwitch: AnimationScalesSwitch

    override fun apply(target: Project) {
        extension = target.extensions.create(EXTENSION_NAME, TestDeviceManagerExtension::class.java)

        project = target

        target.afterEvaluate { project ->
            project.allprojects { subProject ->
                androidAppExtension = subProject.extensions.getByType(AppExtension::class.java)

                bridge = androidAppExtension.createAndroidDebugBridge()
                outputReceiverProvider = OutputReceiverProvider()
                communicator = DeviceCommunicator(bridge, outputReceiverProvider)

                init()

                TaskCreator(project,
                            extension,
                            communicator,
                            animationScalesPersistenceHelper,
                            animationScalesSwitch).createTasks()
            }
        }
    }

    private fun init() {
        outDir = File(project.buildDir, OUTPUT_DIRECTORY_PATH)
        configFile = File(project.buildDir, CONFIG_FILE_PATH)
        dataParser = DataParser()
        animationScalesPersistenceHelper = AnimationScalesPersistenceHelper(outDir, configFile, dataParser)
        animationScalesSwitch = AnimationScalesSwitch(animationScalesPersistenceHelper)
    }
}
