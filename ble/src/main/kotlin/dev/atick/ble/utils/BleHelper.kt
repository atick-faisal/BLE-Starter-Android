package dev.atick.ble.utils

import dev.atick.ble.data.BleDevice
import kotlinx.coroutines.flow.Flow

interface BleHelper {
    fun scanForBleDevices(): Flow<BleDevice>
    suspend fun connect(device: BleDevice): Result<Boolean>
}