package dev.atick.ble.data

data class BleService(
    val name: String = "Unknown Service",
    val uuid: String,
    val characteristics: List<BleCharacteristic>
) {
    override fun toString(): String {
        val serviceString = StringBuilder()
        serviceString.append("Name: $name \n")
        serviceString.append("UUID: $uuid \n")
        characteristics.forEach { char ->
            serviceString.append("\t Name: ${char.name} \n")
            serviceString.append("\t\t UUID: ${char.uuid} \n")
            serviceString.append("\t\t Property: ${char.property} \n")
            serviceString.append("\t\t Permission: ${char.permission} \n")
            serviceString.append("\t\t Value: ${char.value} \n")
        }

        return serviceString.toString()
    }
}
