package dev.atick.ble.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanSettings
import dev.atick.ble.data.BleCharacteristic
import dev.atick.ble.data.BleDescriptor
import dev.atick.ble.data.BleService
import dev.atick.core.utils.extensions.toHexString
import java.util.*


@SuppressLint("MissingPermission")
fun BluetoothLeScanner.scan(scanCallback: ScanCallback) {
    val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()
    startScan(null, scanSettings, scanCallback)
}

fun BluetoothGattCharacteristic.simplify(): BleCharacteristic {
    return BleCharacteristic(
        uuid = uuid.toShortString(),
        property = properties.toString(),
        permission = permissions.toString(),
        value = value?.toHexString(),
        descriptors = descriptors?.map { descriptor ->
            BleDescriptor(
                uuid = descriptor.uuid.toShortString(),
                value = descriptor.value?.toHexString()
            )
        } ?: listOf()
    )
}

fun BluetoothGattService.simplify(): BleService {
    return BleService(
        name = uuid.getName(),
        uuid = uuid.toShortString(),
        characteristics =
        characteristics?.map { char ->
            char.simplify()
        } ?: listOf()
    )
}

fun BluetoothGattCharacteristic.isReadable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

fun BluetoothGattCharacteristic.isWritable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

fun BluetoothGattCharacteristic.isIndicatable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_INDICATE)

fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
    return properties and property != 0
}