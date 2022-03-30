package dev.atick.ble.data

import android.bluetooth.BluetoothGatt

sealed class BleCallbacks(val data: Any?) {
    class ConnectionCallback(data: ConnectionStatus):
        BleCallbacks(data)
    class ServicesCallback(data: BluetoothGatt):
        BleCallbacks(data)
}
