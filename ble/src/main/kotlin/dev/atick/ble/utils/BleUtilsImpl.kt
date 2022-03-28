package dev.atick.ble.utils

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.orhanobut.logger.Logger
import dev.atick.core.utils.extensions.hasPermission
import dev.atick.core.utils.extensions.permissionLauncher
import dev.atick.core.utils.extensions.showAlertDialog
import javax.inject.Inject

class BleUtilsImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter?
) : BleUtils {

    private val isBluetoothAvailable: Boolean = bluetoothAdapter == null
    private lateinit var blePermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun initialize(
        activity: ComponentActivity,
        onSuccess: () -> Unit
    ) {
        blePermissionLauncher = activity.permissionLauncher(
            onSuccess = {
                Logger.i("Bluetooth Permission Granted")
                showLocationPermissionRationale(activity)
            },
            onFailure = { activity.finishAffinity() }
        )
        locationPermissionLauncher = activity.permissionLauncher(
            onSuccess = {
                Logger.i("Location Permission Granted")
                onSuccess.invoke()
            },
            onFailure = { activity.finishAffinity() }
        )
    }

    override fun isAllPermissionsProvided(activity: ComponentActivity): Boolean {
        return isBluetoothAvailable &&
            isBluetoothPermissionGranted(activity) &&
            isLocationPermissionGranted(activity)
    }

    override fun askForPermissions(activity: ComponentActivity) {
        Logger.i("CALLED")
        if (bluetoothAdapter == null) activity.finishAffinity()
        if (!isBluetoothPermissionGranted(activity)) {
            showBluetoothPermissionRationale(activity)
        } else {
            showLocationPermissionRationale(activity)
        }
    }

    override fun enableBluetooth(activity: ComponentActivity, onSuccess: () -> Unit) {
        TODO("Not yet implemented")
    }

    private fun askForBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blePermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun askForLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun showBluetoothPermissionRationale(activity: ComponentActivity) {
        if (!isBluetoothPermissionGranted(activity)) {
            activity.showAlertDialog(
                title = "Enable Bluetooth",
                message = "This app requires Bluetooth connection " +
                    "to work properly. Please enable Bluetooth.",
                onApprove = {
                    Logger.i("Bluetooth Rationale Approved")
                    askForBluetoothPermission()
                },
                onCancel = { activity.finishAffinity() }
            )
        }
    }

    private fun showLocationPermissionRationale(activity: ComponentActivity) {
        if (!isLocationPermissionGranted(activity)) {
            activity.showAlertDialog(
                title = "Provide Location Access",
                message = "Scanning for BLE devices require location Access. " +
                    "This is only required for Bluetooth Scanning and your " +
                    "location information will NOT be used for tracking.",
                onApprove = {
                    Logger.i("Location Rationale Approved")
                    askForLocationPermission()
                },
                onCancel = { activity.finishAffinity() }
            )
        }
    }

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
}