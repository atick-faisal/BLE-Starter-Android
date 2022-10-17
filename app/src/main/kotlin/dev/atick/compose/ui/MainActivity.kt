package dev.atick.compose.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orhanobut.logger.Logger
import com.welie.blessed.BluetoothCentralManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.ble.data.BleDevice
import dev.atick.ble.utils.BleHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var bleHelper: BleHelper

    @Inject
    lateinit var centralManager: BluetoothCentralManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel()
) {
    val devices = mainViewModel.devices

    Column(Modifier.fillMaxSize()) {
        LazyColumn {
            items(devices) { device ->
                Button(onClick = { mainViewModel.connect(device) }) {
                    Text(text = device.name)
                }
            }
        }
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(private val bleHelper: BleHelper) : ViewModel() {
    var devices = mutableStateListOf<BleDevice>()

    init {
        viewModelScope.launch {
            bleHelper.scanForBleDevices().collect { device ->
                if (device !in devices) devices.add(device)
            }
        }
    }

    fun connect(device: BleDevice) {
        viewModelScope.launch {
            val result = bleHelper.connect(device)
            if (result.isSuccess) {
                Logger.i("CONNECTED!")
            }
        }
    }
}