package dev.atick.ble.repository

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import com.orhanobut.logger.Logger
import dev.atick.ble.data.BLEDevice
import dev.atick.core.utils.extensions.hasPermission
import dev.atick.core.utils.extensions.permissionLauncher
import dev.atick.core.utils.extensions.resultLauncher
import dev.atick.core.utils.extensions.showAlertDialog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BLEManagerImpl @Inject constructor() : BLEManager {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bleScanner: BluetoothLeScanner
    private val scanSettings = ScanSettings
        .Builder()

        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()
    private val scanResults = mutableListOf<ScanResult>()

    private fun isBluetoothPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
        } else true
    }

    private fun isLocationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        } else true
    }

    override fun initializeBluetooth(context: Context) {
        val bluetoothManager = context.getSystemService(
            Context.BLUETOOTH_SERVICE
        ) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    override fun enableBluetooth(
        activity: ComponentActivity,
        onSuccess: () -> Unit
    ) {
        if (!bluetoothAdapter.isEnabled) {
            if (isBluetoothPermissionGranted(activity)) {
                requestEnableBluetooth(activity, onSuccess)
            } else {
                askForBluetoothPermissions(
                    activity = activity,
                    onSuccess = {
                        requestEnableBluetooth(activity, onSuccess)
                    }
                )
            }
        } else {
            if (!isLocationPermissionGranted(activity)) {
                askForLocationPermission(
                    activity = activity,
                    onSuccess = onSuccess
                )
            }
        }
    }


    override fun scanDevices(
        activity: ComponentActivity,
        autoConnect: Boolean
    ): Flow<List<BLEDevice>> {
        @OptIn(ExperimentalCoroutinesApi::class)
        return callbackFlow {
            val scanCallback = object : ScanCallback() {

                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    if (!isBluetoothPermissionGranted(activity)) return
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

            scanResults.clear()
            bleScanner = bluetoothAdapter.bluetoothLeScanner
            bleScanner.startScan(null, scanSettings, scanCallback)
        }
    }

    private fun requestEnableBluetooth(
        activity: ComponentActivity,
        onSuccess: () -> Unit
    ) {
        val enableIntent = Intent(
            BluetoothAdapter.ACTION_REQUEST_ENABLE
        )
        val resultLauncher = activity.resultLauncher(
            onSuccess = {
                askForLocationPermission(activity, onSuccess)
            },
            onFailure = { activity.finishAffinity() }
        )
        activity.showAlertDialog(
            title = "Enable Bluetooth",
            message = "This app requires Bluetooth connection " +
                "to work properly. Please enable Bluetooth.",
            onApprove = {
                resultLauncher.launch(enableIntent)
            },
            onCancel = { activity.finishAffinity() }
        )
    }

    private fun askForBluetoothPermissions(
        activity: ComponentActivity,
        onSuccess: () -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissionLauncher = activity.permissionLauncher(
                onSuccess = onSuccess,
                onFailure = { activity.finishAffinity() }
            )
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        }
    }

    private fun askForLocationPermission(
        activity: ComponentActivity,
        onSuccess: () -> Unit
    ) {
        val permissionLauncher = activity.permissionLauncher(
            onSuccess = onSuccess,
            onFailure = { activity.finishAffinity() }
        )

        activity.showAlertDialog(
            title = "Provide Location Access",
            message = "Scanning for BLE devices require location Access. " +
                "This is only required for Bluetooth Scanning and your " +
                "location information will NOT be used for tracking.",
            onApprove = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            },
            onCancel = { activity.finishAffinity() }
        )
    }
}