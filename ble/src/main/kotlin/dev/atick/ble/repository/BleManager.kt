package dev.atick.ble.repository

import android.content.Context
import dev.atick.ble.data.BleDevice
import dev.atick.ble.data.BleService
import dev.atick.ble.data.ConnectionStatus

interface BleManager {

    /////////////////////////////////////////////////////////

    fun setBleCallbacks(
        onDeviceFound: (BleDevice) -> Unit,
        onConnectionChange: (ConnectionStatus) -> Unit,
        onServiceDiscovered: (List<BleService>) -> Unit
    )

    fun startScan()
    fun connect(context: Context, address: String)
    fun discoverServices()
    fun stopScan()

    /////////////////////////////////////////////////////////

//    val bleCallbacks: Flow<BleCallbacks>
//
//    fun scanForDevices(): Flow<List<BluetoothDevice>>
//    fun connect(context: Context, deviceAddress: String)
//    fun discoverServices()
//    fun stopScan()
}