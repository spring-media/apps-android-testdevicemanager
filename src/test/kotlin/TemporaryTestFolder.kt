import org.junit.rules.TemporaryFolder
import java.io.File

class TemporaryTestFolder : TemporaryFolder() {

    fun copyResourceToFile(resourceName: String, fileName: String) {
        val inputStream = javaClass.classLoader.getResourceAsStream(resourceName)
        val file = File(root, fileName)
        file.outputStream().use { inputStream.copyTo(it) }
    }
}