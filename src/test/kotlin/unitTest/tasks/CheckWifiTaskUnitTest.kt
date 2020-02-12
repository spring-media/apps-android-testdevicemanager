package unitTest.tasks

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import internal.DeviceCommunicator
import internal.OutputReceiverProvider
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import tasks.CheckWifiTask
import unitTest.tasks.internal.BaseUnitTest
import tasks.internal.DefaultPluginTask
import java.io.File

class CheckWifiTaskUnitTest : BaseUnitTest() {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    val device: IDevice = mock()
    val bridge: AndroidDebugBridge = mock()
    val outputReceiver: CollectingOutputReceiver = mock()
    val outputReceiverProvider: OutputReceiverProvider = mock()

    val deviceCommunicator = DeviceCommunicator(bridge, outputReceiverProvider)
    val wifi = "wlanName"
    val devices = arrayOf(device)
    val mNetworkInfo = "mNetworkInfo [type: WIFI[], extra: \"$wifi\"]"
    val wifiDumpsysAndroid9 = "mWifiInfo SSID: $wifi, BSSID: 50:06:04:50:06:04, MAC: a8:db:03:a8:db:03, Supplicant state: COMPLETED, RSSI: -62, Link speed: 270Mbps, Frequency: 5180MHz, Net ID: 2, Metered hint: false, GigaAp: false, VenueName: null, WifiMode: 4, score: 60\n" +
            "mDhcpResults IP address xxx.xx.xx.xx/20 Gateway xxx.xx.xx.xx  DNS servers: [ xxx.xx.xx.xx 8.8.8.8 8.8.4.4 ] Domains asv.cor DHCP server /xxx.xx.xx.xx Vendor info null lease 1800 seconds\n" +
            "mNetworkInfo [type: WIFI[], state: CONNECTED/CONNECTED, reason: (unspecified), extra: (none), failover: false, available: true, roaming: false]\n"



    lateinit var projectDir: File
    lateinit var project: Project
    lateinit var task: CheckWifiTask

    @Before
    fun setup() {
        projectDir = temporaryFolder.root
        projectDir.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        task = project.tasks.create("CheckWifiTask", CheckWifiTask::class.java)

        task.communicator = deviceCommunicator
        task.wifi = wifi

        given(outputReceiverProvider.get()).willReturn(outputReceiver)
        given(bridge.devices).willReturn(devices)

    }

    @Test(expected = GradleException::class)
    fun `throw gradle exception when string in extension is empty`() {
        task.wifi = ""

        task.runTask1()
    }

    @Test(expected = GradleException::class)
    fun `throw gradle exception when string in extension is blank`() {
        task.wifi = " "

        task.runTask1()
    }

    @Test
    fun `no gradle exception is thrown when connected to right wifi`() {
        given(outputReceiver.output).willReturn(mNetworkInfo)

        task.runTask2(device)

        thenDeviceShouldGetDetails(device)
    }

    @Test
    fun `no gradle exception is thrown when connected to right wifi on Android 9 and 10`() {
        given(outputReceiver.output).willReturn(wifiDumpsysAndroid9)

        task.runTask2(device)

        thenDeviceShouldGetDetails(device)
    }
}