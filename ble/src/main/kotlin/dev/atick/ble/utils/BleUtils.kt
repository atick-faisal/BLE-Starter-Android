package dev.atick.ble.utils

import androidx.activity.ComponentActivity

interface BleUtils {

    fun initialize(
        activity: ComponentActivity,
        onSuccess: () -> Unit = {}
    )

    fun isAllPermissionsProvided(activity: ComponentActivity): Boolean

    fun askForPermissions(activity: ComponentActivity)

    fun enableBluetooth(
        activity: ComponentActivity,
        onSuccess: () -> Unit = {}
    )

}