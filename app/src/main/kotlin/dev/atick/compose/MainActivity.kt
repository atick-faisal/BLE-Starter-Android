package dev.atick.compose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.ble.utils.BleUtils
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var bleUtils: BleUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bleUtils.initialize(this) {
            Logger.i("Bluetooth Setup Successful!")
        }
    }

    override fun onResume() {
        super.onResume()
        bleUtils.setupBluetooth(this)
    }
}