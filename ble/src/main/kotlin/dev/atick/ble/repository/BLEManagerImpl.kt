package dev.atick.ble.repository

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import dev.atick.core.utils.extensions.hasPermission
import dev.atick.core.utils.extensions.permissionLauncher
import dev.atick.core.utils.extensions.resultLauncher
import dev.atick.core.utils.extensions.showAlertDialog
import javax.inject.Inject

class BLEManagerImpl @Inject constructor() : BLEManager {

    private lateinit var bluetoothAdapter: BluetoothAdapter

    private fun isPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
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
            if (isPermissionGranted(activity)) {
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
            askForLocationPermission(
                activity = activity,
                onSuccess = onSuccess
            )
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