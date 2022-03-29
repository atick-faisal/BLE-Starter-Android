package dev.atick.compose.ui.device

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.ble.repository.BleManager
import dev.atick.core.ui.BaseViewModel
import javax.inject.Inject


@HiltViewModel
class DeviceViewModel @Inject constructor(
    bleManager: BleManager
): BaseViewModel() {

}