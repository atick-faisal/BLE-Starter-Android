package dev.atick.ble.repository

import android.bluetooth.BluetoothDevice
import android.content.Context
import dev.atick.ble.data.ConnectionStatus
import kotlinx.coroutines.flow.Flow

interface BleManager {
    fun scanForDevices(): Flow<List<BluetoothDevice>>
    fun connect(context: Context, device: BluetoothDevice): Flow<ConnectionStatus>
    fun stopScan()
}