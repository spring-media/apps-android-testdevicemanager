package internal

import com.winterbe.expekt.should
import org.junit.Test

class DataParserTest {

    private val classToTest = DataParser()

    private val configFileString = "androidId 1.0 1.0 1.0"
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
}