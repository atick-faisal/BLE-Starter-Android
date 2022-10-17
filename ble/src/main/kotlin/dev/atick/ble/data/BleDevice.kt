package dev.atick.ble.data

import com.welie.blessed.BluetoothPeripheral

data class BleDevice(
    val name: String,
    val address: String,
    val rssi: Int,
    val peripheral: BluetoothPeripheral
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as BleDevice
        return address == other.address
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}