package tasks.internal

import com.android.ddmlib.IDevice
import com.nhaarman.mockito_kotlin.then
import org.mockito.internal.verification.Times

open class BaseTest {

    fun thenDeviceShouldGetDetails(device: IDevice) {
        then(device).should(Times(1)).getProperty("ro.product.model")
        then(device).should(Times(1)).getProperty("ro.build.version.release")
        then(device).should(Times(1)).getProperty("ro.build.version.sdk")
    }
}
