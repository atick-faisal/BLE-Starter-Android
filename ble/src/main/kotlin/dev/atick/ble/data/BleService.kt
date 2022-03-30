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
            serviceString.append("|__ Name: ${char.name} \n")
            serviceString.append("|__UUID: ${char.uuid} \n")
            serviceString.append("|__ Property: ${char.property} \n")
            serviceString.append("|__ Permission: ${char.permission} \n")
            serviceString.append("|__ Value: ${char.value} \n")
            char.descriptors.forEach { descriptor ->
                serviceString.append(".     |__ UUID: ${descriptor.uuid} \n")
                serviceString.append(".     |__ Value: ${descriptor.value} \n")
            }
        }

        return serviceString.toString()
    }
}
