package unitTest.internal

import com.winterbe.expekt.should
import internal.DataParser
import org.gradle.api.GradleException
import org.junit.Test

class DataParserTest {

    private val classToTest = DataParser()

    private val configFileString = "androidId 1.0 1.0 1.0"
    private val configFileString2 = "androidId 1.0 1.0"
    private val animationScales = hashMapOf(
            "window_animation_scale" to 1.0F,
            "transition_animation_scale" to 1.0F,
            "animator_duration_scale" to 1.0F
    )

    @Test
    fun `can get animation scales`() {
        val result = classToTest.getAnimationScalesFrom(configFileString)

        result.should.equal(animationScales)
    }

    @Test(expected = GradleException::class)
    fun `can throw gradle exception when regex cannot find string`() {
        classToTest.getAnimationScalesFrom(configFileString2)
    }
}