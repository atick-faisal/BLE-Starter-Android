package dev.atick.ble.data

data class BleCharacteristic(
    val name: String = "Unknown Characteristic",
    val uuid: String,
    val property: String,
    val permission: String,
    val value: String? = null
)
