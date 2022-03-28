package dev.atick.ble.utils

import android.Manifest
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
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun initialize(
        activity: ComponentActivity,
        onSuccess: () -> Unit
    ) {
        permissionLauncher = activity.permissionLauncher(
            onSuccess = {
                Logger.i("All Permissions Granted")
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
        showPermissionRationale(activity)
    }

    override fun enableBluetooth(activity: ComponentActivity, onSuccess: () -> Unit) {
        TODO("Not yet implemented")
    }

    private fun askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun showPermissionRationale(activity: ComponentActivity) {
        if (!isAllPermissionsProvided(activity)) {
            activity.showAlertDialog(
                title = "Permission Required",
                message = "This app requires Bluetooth connection " +
                    "to work properly. Please provide Bluetooth permission. " +
                    "Scanning for BLE devices also requires Location Access " +
                    "Permission. However, location information will NOT be" +
                    "used for tracking.",
                onApprove = {
                    Logger.i("Permission Rationale Approved")
                    askForPermissions()
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