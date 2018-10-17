package tasks.internal

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import internal.DeviceCommunicator
import internal.OutputReceiverProvider
import internal.TaskInfo.GROUP_NAME
import internal.devicesCanBeFound
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


abstract class DefaultPluginTask : DefaultTask() {

    init {
        group = GROUP_NAME
    }

    var communicator: DeviceCommunicator? = null
        set(value) {
            value?.let {
                bridge = it.bridge
                outputReceiverProvider = it.outputReceiverProvider
            }
        }

    lateinit var bridge: AndroidDebugBridge
    lateinit var outputReceiverProvider: OutputReceiverProvider

    @TaskAction
    fun performTask() {

        runTask1()

        bridge.devicesCanBeFound()

        runTask2()

        bridge.devices.forEach { device ->
            runTaskFor(device)
        }

        runPostTask()
    }

    abstract fun runTask1()

    abstract fun runTask2()

    abstract fun runTaskFor(device: IDevice)

    abstract fun runPostTask()
}