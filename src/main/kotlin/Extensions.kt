import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice

fun IDevice.executeShellCommandWithOutput(shellCommand: String): String {
    val outputReceiver = CollectingOutputReceiver()

    this.executeShellCommand(shellCommand, outputReceiver)

    return outputReceiver.output
}