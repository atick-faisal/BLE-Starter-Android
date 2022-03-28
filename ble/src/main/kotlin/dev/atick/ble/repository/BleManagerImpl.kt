package dev.atick.ble.repository

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.orhanobut.logger.Logger
import dev.atick.ble.data.ConnectionStatus
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
    private val scanResults = mutableListOf<BluetoothDevice>()
    private var bluetoothGatt: BluetoothGatt? = null

    override fun scanForDevices(): Flow<List<BluetoothDevice>> {
        return callbackFlow {
            scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    val indexQuery = scanResults.indexOfFirst { device ->
                        device.address == result.device.address
                    }
                    if (indexQuery != -1) {
                        scanResults[indexQuery] = result.device
                    } else {
                        Logger.i("Found device: $result")
                        result.device?.let { device ->
                            scanResults.add(device)
                            trySend(scanResults)
                        }
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

    override fun connect(
        context: Context,
        device: BluetoothDevice
    ): Flow<ConnectionStatus> =
        callbackFlow {
            val connectionCallback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(
                    gatt: BluetoothGatt?,
                    status: Int,
                    newState: Int
                ) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        when (newState) {
                            BluetoothProfile.STATE_CONNECTING -> {
                                Logger.i("Connecting")
                                trySend(ConnectionStatus.CONNECTING)
                                bluetoothGatt = gatt
                            }
                            BluetoothProfile.STATE_CONNECTED -> {
                                Logger.i("Connected")
                                trySend(ConnectionStatus.CONNECTED)
                            }
                            BluetoothProfile.STATE_DISCONNECTED -> {
                                Logger.i("Disconnected")
                                trySend(ConnectionStatus.DISCONNECTED)
                                gatt?.close()
                            }
                        }
                    } else {
                        Logger.i("Failed to Connect")
                        trySend(ConnectionStatus.DISCONNECTED)
                        gatt?.close()
                    }
                }
            }

            device.connectGatt(context, false, connectionCallback)

            awaitClose {
                bluetoothGatt?.close()
            }
        }


    override fun stopScan() {
        bleScanner?.stopScan(scanCallback)
    }
}