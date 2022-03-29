package dev.atick.ble.utils

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import com.orhanobut.logger.Logger
import dev.atick.ble.data.BleCallbacks
import dev.atick.ble.data.ConnectionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


@kotlinx.coroutines.ExperimentalCoroutinesApi
fun getGattCallbacks(): Flow<BleCallbacks> {
    return callbackFlow {
        object : BluetoothGattCallback() {
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
    }
}