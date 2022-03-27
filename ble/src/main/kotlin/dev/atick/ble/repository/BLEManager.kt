package dev.atick.ble.repository

import android.content.Context
import androidx.activity.ComponentActivity
import dev.atick.ble.data.BLEDevice
import kotlinx.coroutines.flow.Flow

interface BLEManager {
    fun initializeBluetooth(context: Context)
    fun enableBluetooth(
        activity: ComponentActivity,
        onSuccess: () -> Unit = {}
    )
    fun scanDevices(
        activity: ComponentActivity,
        autoConnect: Boolean
    ): Flow<List<BLEDevice>>
}