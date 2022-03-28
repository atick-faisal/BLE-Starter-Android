package dev.atick.ble.repository

import dev.atick.ble.data.BLEDevice
import kotlinx.coroutines.flow.Flow

interface BLEManager {

    fun scanDevices(): Flow<List<BLEDevice>>
}