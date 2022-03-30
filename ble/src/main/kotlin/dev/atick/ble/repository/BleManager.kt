package dev.atick.ble.repository

import android.content.Context
import androidx.lifecycle.LiveData
import dev.atick.ble.data.BleCharacteristic
import dev.atick.ble.data.BleDevice
import dev.atick.ble.data.BleService
import dev.atick.ble.data.ConnectionStatus
import dev.atick.core.utils.Event

interface BleManager {

    val loading: LiveData<Event<Boolean>>

    fun setBleCallbacks(
        onDeviceFound: (BleDevice) -> Unit,
        onConnectionChange: (ConnectionStatus) -> Unit,
        onServiceDiscovered: (List<BleService>) -> Unit,
        onCharacteristicRead: (BleCharacteristic) -> Unit,
        onCharacteristicChange: (BleCharacteristic) -> Unit,
        onCharacteristicWrite: (BleCharacteristic) -> Unit = {}
    )

    fun startScan()
    fun stopScan()
    fun connect(context: Context, address: String)
    fun disconnect()
    fun discoverServices()
    fun readCharacteristic(serviceUuid: String, charUuid: String)
    fun writeCharacteristic(serviceUuid: String, charUuid: String, payload: ByteArray)
    fun enableNotification(serviceUuid: String, charUuid: String)
    fun disableNotification(serviceUuid: String, charUuid: String)

}