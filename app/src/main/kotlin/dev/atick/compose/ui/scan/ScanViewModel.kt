package dev.atick.compose.ui.scan

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.ble.data.BleDevice
import dev.atick.ble.repository.BleManager
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.extensions.stateInDelayed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
@SuppressLint("MissingPermission")
class ScanViewModel @Inject constructor(
    bleManager: BleManager
) : BaseViewModel() {

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
                initialValue = listOf<BleDevice>(),
                scope = viewModelScope
            )

}