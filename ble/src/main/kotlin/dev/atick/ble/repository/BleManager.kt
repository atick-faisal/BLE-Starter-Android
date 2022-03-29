package dev.atick.ble.repository

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import dev.atick.ble.data.ConnectionStatus
import kotlinx.coroutines.flow.Flow

interface BleManager {
    fun scanForDevices(): Flow<List<BluetoothDevice>>
    fun connect(context: Context, deviceAddress: String): Flow<ConnectionStatus>
    fun discoverServices(): Flow<List<BluetoothGattService>>
    fun stopScan()
}