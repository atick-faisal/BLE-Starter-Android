package dev.atick.compose.ui.device

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.ble.data.BleCharacteristic
import dev.atick.ble.data.BleService
import dev.atick.ble.repository.BleManager
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.extensions.stateInDelayed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class DeviceViewModel @Inject constructor(
    bleManager: BleManager
): BaseViewModel() {
    val services: StateFlow<List<BleService>> =
        bleManager.discoverServices()
            .map { serviceList ->
                serviceList.map { service ->
                    BleService(
                        uuid = service.uuid.toString(),
                        characteristics = service.characteristics.map { char ->
                            BleCharacteristic(
                                uuid = char.uuid.toString(),
                                property = char.properties.toString(),
                                permission = char.permissions.toString()
                            )
                        }
                    )
                }
            }
            .stateInDelayed(
                initialValue = listOf(),
                scope = viewModelScope
            )
}