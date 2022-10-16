package dev.atick.ble.utils

import android.bluetooth.le.ScanResult
import com.orhanobut.logger.Logger
import com.welie.blessed.BluetoothCentralManager
import com.welie.blessed.BluetoothPeripheral
import com.welie.blessed.ScanFailure
import dev.atick.ble.data.BleDevice
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class BleHelperImpl @Inject constructor(
    private val centralManager: BluetoothCentralManager
) : BleHelper {

    override fun scanForBleDevices(): Flow<BleDevice> =
        callbackFlow {
            val resultCallback = { peripheral: BluetoothPeripheral, result: ScanResult ->
                trySend(
                    BleDevice(
                        name = peripheral.name,
                        address = peripheral.address,
                        rssi = result.rssi,
                        peripheral = peripheral
                    )
                )
                Unit
            }

            val failureCallback = { error: ScanFailure ->
                Logger.e("SCAN ERROR: $error")
                channel.close()
                Unit
            }

            centralManager.scanForPeripherals(resultCallback, failureCallback)

            awaitClose {
                Logger.w("STOPPING BLE SCAN ... ")
                centralManager.stopScan()
            }
        }

}