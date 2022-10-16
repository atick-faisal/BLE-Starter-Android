package dev.atick.compose.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.orhanobut.logger.Logger
import com.welie.blessed.BluetoothCentralManager
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.ble.utils.BleHelper
import dev.atick.compose.R
import dev.atick.core.utils.extensions.stateInDelayed
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var bleHelper: BleHelper

    @Inject
    lateinit var centralManager: BluetoothCentralManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val devices = bleHelper.scanForBleDevices()

        val scope = lifecycleScope.launchWhenStarted {
            Logger.i("SCANNING ... ")
            devices.collect { device ->
                Logger.i("FOUND: $device")
            }
        }

        lifecycleScope.launchWhenStarted {
            delay(5000L)
            scope.cancel()
        }
    }
}