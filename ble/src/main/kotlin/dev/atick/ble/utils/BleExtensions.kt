package dev.atick.ble.utils

import android.annotation.SuppressLint
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanSettings
import java.util.*


@SuppressLint("MissingPermission")
fun BluetoothLeScanner.scan(scanCallback: ScanCallback) {
    val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()
    startScan(null, scanSettings, scanCallback)
}

fun UUID.toShortString(): String {
    return toString().subSequence(0, 8).toString()
}