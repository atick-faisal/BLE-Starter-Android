package dev.atick.compose.ui.device

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.ble.data.BleCallbacks
import dev.atick.ble.data.BleCharacteristic
import dev.atick.ble.data.BleService
import dev.atick.ble.repository.BleManager
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.extensions.stateInDelayed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val bleManager: BleManager
) : BaseViewModel() {

    init {
        discoverServices()
    }

    val services: StateFlow<List<BleService>> =
        bleManager.bleCallbacks
            .filter { it is BleCallbacks.ServicesCallback }
            .map { it.data as BluetoothGatt }
            .map { it.services ?: listOf() }
            .map { serviceList ->
                serviceList.map { service ->
                    with(service as BluetoothGattService) {
                        BleService(
                            uuid = uuid.toString(),
                            characteristics = characteristics.map { char ->
                                BleCharacteristic(
                                    uuid = char.uuid.toString(),
                                    property = char.properties.toString(),
                                    permission = char.permissions.toString()
                                )
                            }
                        )
                    }
                }
            }
            .stateInDelayed(
                initialValue = listOf(),
                scope = viewModelScope
            )

//    val services: SharedFlow<BluetoothGatt> = bleManager.bleCallbacks
//        .filter { it is BleCallbacks.ServicesCallback }
//        .map { it.data as BluetoothGatt }
//        .shareInDelayed(viewModelScope)

    private fun discoverServices() {
        bleManager.discoverServices()
    }
}