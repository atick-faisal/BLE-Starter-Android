package dev.atick.ble.data

data class BleService(
    val name: String = "Unknown Service",
    val uuid: String,
    val characteristics: List<BleCharacteristic>
)
