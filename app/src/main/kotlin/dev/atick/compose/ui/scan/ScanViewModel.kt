package dev.atick.compose.ui.scan

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.ble.data.BleDevice
import dev.atick.ble.data.ConnectionStatus
import dev.atick.ble.repository.BleManager
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.extensions.stateInDelayed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
@SuppressLint("MissingPermission")
class ScanViewModel @Inject constructor(
    private val bleManager: BleManager
) : BaseViewModel() {

    lateinit var connectionStatus: StateFlow<ConnectionStatus>

    val devices: StateFlow<List<BleDevice>> =
        bleManager.scanForDevices()
            .map { deviceList ->
                deviceList.map { device ->
                    BleDevice(
                        name = device.name ?: "Unnamed",
                        address = device.address ?: "Can't Access"
                    )
                }
            }
            .stateInDelayed(
                initialValue = listOf(),
                scope = viewModelScope
            )


    fun connect(context: Context, deviceAddress: String) {
        connectionStatus = bleManager.connect(context, deviceAddress)
            .stateInDelayed(
                initialValue = ConnectionStatus.DISCONNECTED,
                scope = viewModelScope
            )
    }
}