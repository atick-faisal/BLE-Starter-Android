package dev.atick.ble.repository

import android.content.Context
import androidx.activity.ComponentActivity

interface BLEManager {
    fun initializeBluetooth(context: Context)
    fun enableBluetooth(
        activity: ComponentActivity,
        onSuccess: () -> Unit = {}
    )
}