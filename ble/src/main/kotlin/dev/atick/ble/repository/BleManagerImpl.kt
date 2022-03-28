package dev.atick.ble.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.orhanobut.logger.Logger
import dev.atick.ble.data.BLEDevice
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


@SuppressLint("MissingPermission")
@kotlinx.coroutines.ExperimentalCoroutinesApi
class BleManagerImpl @Inject constructor(
    bluetoothAdapter: BluetoothAdapter?,
) : BleManager {

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private lateinit var scanCallback: ScanCallback
    private val bleScanner = bluetoothAdapter?.bluetoothLeScanner
    private val scanResults = mutableListOf<ScanResult>()

    override fun scanForDevices(): Flow<List<BLEDevice>> {
        return callbackFlow {
            scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    val indexQuery = scanResults.indexOfFirst {
                        it.device.address == result.device.address
                    }
                    if (indexQuery != -1) {
                        scanResults[indexQuery] = result
                    } else {
                        Logger.i("Found device: $result")
                        val devices = scanResults.map { scanResult ->
                            BLEDevice(
                                name = scanResult.device?.name ?: "Unnamed",
                                address = scanResult.device?.address ?: "null"
                            )
                        }
                        scanResults.add(result)
                        trySend(devices)
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    Logger.e("onScanFailed: code $errorCode")
                }
            }

            bleScanner?.let { scanner ->
                scanResults.clear()
                scanner.startScan(null, scanSettings, scanCallback)
            }

            awaitClose {
                stopScan()
            }
        }
    }

    override fun stopScan() {
        bleScanner?.stopScan(scanCallback)
    }
}