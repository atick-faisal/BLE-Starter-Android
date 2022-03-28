package dev.atick.ble.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.orhanobut.logger.Logger
import dev.atick.ble.data.BLEDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


@SuppressLint("MissingPermission")
@kotlinx.coroutines.ExperimentalCoroutinesApi
class BLEManagerImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter?,
) : BLEManager {

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val scanResults = mutableListOf<ScanResult>()


    override fun scanDevices(): Flow<List<BLEDevice>> {
        return callbackFlow {
            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    val indexQuery = scanResults.indexOfFirst {
                        it.device.address == result.device.address
                    }
                    if (indexQuery != -1) {
                        scanResults[indexQuery] = result
                    } else {
                        val devices = scanResults.map { scanResult ->
                            BLEDevice(
                                name = scanResult.device?.name ?: "Unnamed",
                                address = scanResult.device?.address ?: "null"
                            )
                        }
                        trySend(devices)
                        scanResults.add(result)
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    Logger.e("onScanFailed: code $errorCode")
                }
            }

            bluetoothAdapter?.let { adapter ->
                scanResults.clear()
                val bleScanner = adapter.bluetoothLeScanner
                bleScanner.startScan(null, scanSettings, scanCallback)
            }
        }
    }
}