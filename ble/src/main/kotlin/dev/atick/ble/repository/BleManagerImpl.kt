package dev.atick.ble.repository

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.orhanobut.logger.Logger
import dev.atick.ble.data.BleCallbacks
import dev.atick.ble.data.ConnectionStatus
import kotlinx.coroutines.cancel
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


    ////////////////////////////////////////////////////////////////
    private lateinit var callback: BluetoothGattCallback
    override val bleCallbacks: Flow<BleCallbacks>
        get() = callbackFlow {
            callback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(
                    gatt: BluetoothGatt?,
                    status: Int,
                    newState: Int
                ) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        when (newState) {
                            BluetoothProfile.STATE_CONNECTING -> {
                                Logger.i("Connecting")
                                trySend(BleCallbacks.ConnectionCallback(
                                    ConnectionStatus.CONNECTING
                                ))
                            }
                            BluetoothProfile.STATE_CONNECTED -> {
                                Logger.i("Connected")
                                trySend(BleCallbacks.ConnectionCallback(
                                    ConnectionStatus.CONNECTED
                                ))
                                bluetoothGatt = gatt
                            }
                            BluetoothProfile.STATE_DISCONNECTED -> {
                                Logger.i("Disconnected")
                                trySend(BleCallbacks.ConnectionCallback(
                                    ConnectionStatus.DISCONNECTED
                                ))
                            }
                        }
                    } else {
                        Logger.e("Failed to Connect")
                        trySend(BleCallbacks.ConnectionCallback(
                            ConnectionStatus.DISCONNECTED
                        ))
                    }
                }
            }

            awaitClose {
                bluetoothGatt?.close()
            }
        }












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
        deviceAddress: String
    ) {
        Logger.i("Connecting ... ")
        scanResults.forEach {
            if (deviceAddress == it.address) {
                it.connectGatt(context, false, callback)
            }
        }
    }


    override fun discoverServices(): Flow<List<BluetoothGattService>> {
        return callbackFlow {
            object: BluetoothGattCallback() {
                override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                    Logger.i("Size : ${gatt?.services?.size}")
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        bluetoothGatt?.let {
                            trySend(it.services ?: listOf())
                        }
                    }
                    else {
                        Logger.e("Service Discovery Failed")
                        bluetoothGatt?.close()
                    }
                }
            }

            Logger.i("Discovering Services $bluetoothGatt")
            bluetoothGatt?.discoverServices()

            awaitClose {
                bluetoothGatt?.close()
            }
        }
    }


    override fun stopScan() {
        bleScanner?.stopScan(scanCallback)
    }
}