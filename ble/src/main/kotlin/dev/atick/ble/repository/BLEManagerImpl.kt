package dev.atick.ble.repository

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import dev.atick.core.utils.extensions.hasPermission
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
                val enableIntent = Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE
                )
                val resultLauncher = activity.resultLauncher(
                    onSuccess = onSuccess,
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

            } else {
                askForPermissions()
            }
        }
    }

    private fun askForPermissions() {
        TODO("Not yet implemented")
    }
}