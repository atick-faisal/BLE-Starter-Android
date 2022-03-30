package dev.atick.compose

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.atick.ble.data.BleCharacteristic
import dev.atick.ble.data.BleDevice
import dev.atick.ble.data.BleService
import dev.atick.ble.data.ConnectionStatus
import dev.atick.ble.repository.BleManager
import dev.atick.ble.utils.BleUtils
import dev.atick.core.ui.BaseViewModel
import dev.atick.core.utils.Event
import dev.atick.core.utils.extensions.observeEvent
import dev.atick.core.utils.extensions.showToast
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("MissingPermission")
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var bleUtils: BleUtils
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }

        bleUtils.initialize(this) {
            Logger.i("Bluetooth Setup Successful!")
        }

        observeEvent(viewModel.toastMessage) { message ->
            showToast(message)
        }
    }

    override fun onResume() {
        super.onResume()
        bleUtils.setupBluetooth(this)
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val bleManager: BleManager
) : BaseViewModel() {

    companion object {
        const val BATTERY_SERVICE_UUID = "0000180f-0000-1000-8000-00805f9b34fb"
        const val BATTERY_LEVEL_UUID = "00002a19-0000-1000-8000-00805f9b34fb"

        const val HEART_RATE_SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb"
        const val HEART_RATE_UUID = "00002a37-0000-1000-8000-00805f9b34fb"
    }


    val devices = mutableStateListOf<BleDevice>()
    val bleServices = mutableStateListOf<BleService>()
    var connectionStatus by mutableStateOf(ConnectionStatus.DISCONNECTED)
    var isConnected by mutableStateOf(false)


    init {
        bleManager.setBleCallbacks(
            onDeviceFound = ::onDeviceFound,
            onConnectionChange = ::onConnectionChange,
            onServiceDiscovered = ::onServiceDiscovered,
            onCharacteristicRead = ::onCharacteristicRead,
            onCharacteristicChange = ::onCharacteristicChange
        )
    }

    fun startScan() {
        bleManager.startScan()
    }

    fun connect(context: Context, address: String) {
        bleManager.connect(context, address)
    }

    fun discoverServices() {
        bleManager.discoverServices()
    }

    fun readCharacteristic(
        serviceUuid: String = BATTERY_SERVICE_UUID,
        charUuid: String = BATTERY_LEVEL_UUID
    ) {
        bleManager.readCharacteristic(serviceUuid, charUuid)
    }

    fun enableNotification(
        serviceUuid: String = HEART_RATE_SERVICE_UUID,
        charUuid: String = HEART_RATE_UUID
    ) {
        bleManager.enableNotification(serviceUuid, charUuid)
    }

    fun disableNotification(
        serviceUuid: String = HEART_RATE_SERVICE_UUID,
        charUuid: String = HEART_RATE_UUID
    ) {
        bleManager.disableNotification(serviceUuid, charUuid)
    }

    fun stopScan() {
        bleManager.stopScan()
    }

    private fun onDeviceFound(device: BleDevice) {
        devices.add(device)
    }

    private fun onConnectionChange(status: ConnectionStatus) {
        isConnected = status == ConnectionStatus.CONNECTED
        connectionStatus = status
    }

    private fun onServiceDiscovered(services: List<BleService>) {
        services.forEach { service ->
            bleServices.add(service)
        }
    }

    private fun onCharacteristicRead(char: BleCharacteristic) {
        toastMessage.postValue(Event(char.value ?: "null"))
        Logger.i("Value: ${char.uuid} -> ${char.value}")
    }

    private fun onCharacteristicChange(char: BleCharacteristic) {
        toastMessage.postValue(Event(char.value ?: "null"))
        Logger.i("Value: ${char.uuid} -> ${char.value}")
    }

}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val context = LocalContext.current

        Text(text = viewModel.connectionStatus.name)

        AnimatedVisibility(visible = !viewModel.isConnected) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.startScan() }) {
                Text(text = "Scan")
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(viewModel.devices) { device ->
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.connect(context, device.address)
                            viewModel.stopScan()
                        }) {
                        Text(text = "${device.name} \n ${device.address}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        AnimatedVisibility(visible = viewModel.isConnected) {
            Column {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.discoverServices() }
                ) {
                    Text(text = "Discover Services")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.readCharacteristic() }
                ) {
                    Text(text = "Read Battery Level")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.enableNotification() }
                ) {
                    Text(text = "Enable Heart Rate Notification")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.disableNotification() }
                ) {
                    Text(text = "Disable Heart Rate Notification")
                }
            }
        }

        LazyColumn {
            items(viewModel.bleServices) { service ->
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { }
                ) {
                    Text(text = service.toString())
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}