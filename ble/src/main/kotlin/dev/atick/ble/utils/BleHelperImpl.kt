package dev.atick.ble.utils

import android.bluetooth.le.ScanResult
import com.orhanobut.logger.Logger
import com.welie.blessed.BluetoothCentralManager
import com.welie.blessed.BluetoothPeripheral
import com.welie.blessed.ConnectionFailedException
import com.welie.blessed.ScanFailure
import dev.atick.ble.data.BleDevice
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class BleHelperImpl @Inject constructor(
    private val bleManager: BluetoothCentralManager
) : BleHelper {

//    private val foundDevices = mutableListOf<BleDevice>()

    override fun scanForBleDevices(): Flow<BleDevice> =
        callbackFlow {
            val resultCallback = { peripheral: BluetoothPeripheral, result: ScanResult ->
                val foundDevice = BleDevice(
                    name = peripheral.name.ifBlank { "Unnamed" },
                    address = peripheral.address,
                    rssi = result.rssi,
                    peripheral = peripheral
                )

                // ... Update RSSI
//                val index = foundDevices.indexOfFirst { device ->
//                    device.address == foundDevice.address
//                }
//                if (index != -1) {
//                    foundDevices[index] = foundDevice
//                } else {
//                    Logger.i("FOUND BLE DEVICE: ${foundDevice.name}")
//                    foundDevices.add(foundDevice)
//                }

                trySend(foundDevice)
                Unit
            }

            val failureCallback = { error: ScanFailure ->
                Logger.e("SCAN ERROR: $error")
                channel.close()
                Unit
            }

            bleManager.scanForPeripherals(resultCallback, failureCallback)

            awaitClose {
                Logger.w("STOPPING BLE SCAN ... ")
                bleManager.stopScan()
            }
        }

    override suspend fun connect(device: BleDevice): Result<Boolean> {
        return try {
            bleManager.stopScan()
            bleManager.connectPeripheral(device.peripheral)
            Logger.i("CONNECTED TO: ${device.name}")
            Result.success(true)
        } catch (e: ConnectionFailedException) {
            Logger.e("CONNECTION FAILED!")
            Result.failure(e)
        }
    }
}