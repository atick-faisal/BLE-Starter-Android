package dev.atick.ble.data

sealed class BleCallbacks(val data: Any?) {
    class ConnectionCallback(data: ConnectionStatus):
        BleCallbacks(data)
}
