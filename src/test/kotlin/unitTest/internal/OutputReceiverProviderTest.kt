package unitTest.internal

import com.android.ddmlib.CollectingOutputReceiver
import com.winterbe.expekt.should
import internal.OutputReceiverProvider
import org.junit.Test

class OutputReceiverProviderTest {

    private val classToTest = OutputReceiverProvider()

    @Test
    fun `output receiver can be provided`() {

        val result = classToTest.get()

        result.should.be.instanceof(CollectingOutputReceiver::class.java)
    }
}