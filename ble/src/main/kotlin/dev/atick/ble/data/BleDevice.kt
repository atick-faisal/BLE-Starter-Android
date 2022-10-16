package dev.atick.ble.data

import com.welie.blessed.BluetoothPeripheral

data class BleDevice(
    val name: String,
    val address: String,
    val rssi: Int,
    val peripheral: BluetoothPeripheral
)