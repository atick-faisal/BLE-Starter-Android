package dev.atick.ble.repository

import android.content.Context
import dev.atick.ble.data.BleCharacteristic
import dev.atick.ble.data.BleDevice
import dev.atick.ble.data.BleService
import dev.atick.ble.data.ConnectionStatus
import java.util.*

interface BleManager {

    /////////////////////////////////////////////////////////

    fun setBleCallbacks(
        onDeviceFound: (BleDevice) -> Unit,
        onConnectionChange: (ConnectionStatus) -> Unit,
        onServiceDiscovered: (List<BleService>) -> Unit,
        onCharacteristicRead: (BleCharacteristic) -> Unit,
        onCharacteristicChange: (BleCharacteristic) -> Unit,
        onCharacteristicWrite: (BleCharacteristic) -> Unit
    )

    fun startScan()
    fun connect(context: Context, address: String)
    fun discoverServices()
    fun readCharacteristic(serviceUuid: String, charUuid: String)
    fun writeCharacteristic(serviceUuid: String, charUuid: String, payload: ByteArray)
    fun enableNotification(serviceUuid: String, charUuid: String)
    fun disableNotification(serviceUuid: String, charUuid: String)
    fun stopScan()

    /////////////////////////////////////////////////////////

//    val bleCallbacks: Flow<BleCallbacks>
//
//    fun scanForDevices(): Flow<List<BluetoothDevice>>
//    fun connect(context: Context, deviceAddress: String)
//    fun discoverServices()
//    fun stopScan()
}