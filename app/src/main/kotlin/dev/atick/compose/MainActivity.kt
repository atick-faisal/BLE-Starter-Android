package dev.atick.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.ble.data.BLEDevice
import dev.atick.ble.repository.BleManager
import dev.atick.ble.utils.BleUtils
import dev.atick.compose.ui.theme.JetpackComposeStarterTheme
import dev.atick.core.utils.extensions.stateInDelayed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var bleUtils: BleUtils

    @Inject
    lateinit var bleManager: BleManager

    private lateinit var devices: StateFlow<List<BLEDevice>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        devices = bleManager.scanForDevices().stateInDelayed(
            initialValue = listOf(),
            scope = lifecycleScope
        )

        if (!bleUtils.isAllPermissionsProvided(this)) {
            bleUtils.initialize(this) {
                Logger.i("SUCCESS")
                devices = bleManager.scanForDevices().stateInDelayed(
                    initialValue = listOf(),
                    scope = lifecycleScope
                )
            }
        } else {
            Logger.i("Im here")
            devices = bleManager.scanForDevices().stateInDelayed(
                initialValue = listOf(),
                scope = lifecycleScope
            )
        }

        setContent {
            JetpackComposeStarterTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting(devices)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bleUtils.setupBluetooth(this)
    }
}

@Composable
fun Greeting(devices: StateFlow<List<BLEDevice>>) {

    val listItems by devices.collectAsState()

    LazyColumn {
        items(listItems) { item ->
            Text(text = item.name)
        }
    }
}