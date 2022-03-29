package dev.atick.compose.ui.scan

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.ble.data.BleCallbacks
import dev.atick.ble.data.BleDevice
import dev.atick.ble.data.ConnectionStatus
import dev.atick.ble.repository.BleManager
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.extensions.shareInDelayed
import dev.atick.core.utils.extensions.stateInDelayed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
@SuppressLint("MissingPermission")
class ScanViewModel @Inject constructor(
    private val bleManager: BleManager
) : BaseViewModel() {

    val connectionStatus: StateFlow<ConnectionStatus> = bleManager.bleCallbacks
        .filter { it is BleCallbacks.ConnectionCallback }
        .map { it.data as ConnectionStatus }
        .shareInDelayed(viewModelScope)
        .stateInDelayed(
            initialValue = ConnectionStatus.DISCONNECTED,
            scope = viewModelScope
        )

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
        bleManager.connect(context, deviceAddress)
    }
}