package internal

import com.android.ddmlib.CollectingOutputReceiver


class OutputReceiverProvider {
    fun get() = CollectingOutputReceiver()
}