package dev.atick.ble.repository

import dev.atick.ble.data.BLEDevice
import kotlinx.coroutines.flow.Flow

interface BleManager {
    fun scanForDevices(): Flow<List<BLEDevice>>
    fun stopScan()
}