package tasks.internal

import com.android.ddmlib.IDevice
import com.nhaarman.mockito_kotlin.then
import org.mockito.internal.verification.Times

open class BaseTest {

    fun thenDeviceShouldGetDetails(device: IDevice, times: Int = 1) {
        then(device).should(Times(times)).getProperty("ro.product.model")
        then(device).should(Times(times)).getProperty("ro.build.version.release")
        then(device).should(Times(times)).getProperty("ro.build.version.sdk")
    }
}
