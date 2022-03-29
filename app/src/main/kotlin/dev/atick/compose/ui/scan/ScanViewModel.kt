package dev.atick.compose.ui.scan

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.ble.data.*
import dev.atick.ble.repository.BleManager
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.extensions.shareInDelayed
import dev.atick.core.utils.extensions.stateInDelayed
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
@SuppressLint("MissingPermission")
class ScanViewModel @Inject constructor(
    private val bleManager: BleManager
) : BaseViewModel() {

//    val connectionStatus: LiveData<ConnectionStatus> = bleManager.bleCallbacks
//        .filter { it is BleCallbacks.ConnectionCallback }
//        .map { it.data as ConnectionStatus }
//        .asLiveData()
////        .shareInDelayed(viewModelScope)
////        .stateInDelayed(
////            initialValue = ConnectionStatus.DISCONNECTED,
////            scope = viewModelScope
////        )
//
//    val services: LiveData<List<BleService>> =
//        bleManager.bleCallbacks
//            .filter { it is BleCallbacks.ServicesCallback }
//            .map { it.data as BluetoothGatt }
//            .map { it.services ?: listOf() }
//            .map { serviceList ->
//                serviceList.map { service ->
//                    with(service as BluetoothGattService) {
//                        BleService(
//                            uuid = uuid.toString(),
//                            characteristics = characteristics.map { char ->
//                                BleCharacteristic(
//                                    uuid = char.uuid.toString(),
//                                    property = char.properties.toString(),
//                                    permission = char.permissions.toString()
//                                )
//                            }
//                        )
//                    }
//                }
//            }
//            .asLiveData()
////            .shareInDelayed(viewModelScope)
//
//    val devices: StateFlow<List<BleDevice>> =
//        bleManager.scanForDevices()
//            .map { deviceList ->
//                deviceList.map { device ->
//                    BleDevice(
//                        name = device.name ?: "Unnamed",
//                        address = device.address ?: "Can't Access"
//                    )
//                }
//            }
//            .stateInDelayed(
//                initialValue = listOf(),
//                scope = viewModelScope
//            )
//
//
//    fun connect(context: Context, deviceAddress: String) {
//        bleManager.connect(context, deviceAddress)
//    }
}