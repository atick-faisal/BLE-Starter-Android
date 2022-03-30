package dev.atick.ble.repository

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import dev.atick.ble.data.*
import dev.atick.ble.utils.*
import dev.atick.core.utils.Event
import dev.atick.core.utils.extensions.toHexString
import java.util.*
import javax.inject.Inject

@SuppressLint("MissingPermission")
@kotlinx.coroutines.ExperimentalCoroutinesApi
class BleManagerImpl @Inject constructor(
    bluetoothAdapter: BluetoothAdapter?,
) : BleManager {

    companion object {
        const val CCCD_UUID = "00002902-0000-1000-8000-00805f9b34fb"
    }

    private val bleScanner = bluetoothAdapter?.bluetoothLeScanner
    private lateinit var scanCallback: ScanCallback
    private lateinit var gattCallback: BluetoothGattCallback
    private val scanResults = mutableListOf<BluetoothDevice>()
    private var bluetoothGatt: BluetoothGatt? = null

    private val _loading = MutableLiveData<Event<Boolean>>()
    override val loading: LiveData<Event<Boolean>>
        get() = _loading

    override fun startScan() {
        Logger.w("Starting Scan ... ")
        bleScanner?.scan(scanCallback)
    }

    override fun connect(context: Context, address: String) {
        scanResults.forEach { scanResult ->
            if (address == scanResult.address) {
                _loading.postValue(Event(true))
                Logger.w("Connecting ... ")
                scanResult.connectGatt(
                    context,
                    false,
                    gattCallback
                )
            }
        }
    }

    override fun discoverServices() {
        _loading.postValue(Event(true))
        Logger.w("Discovering Services ... ")
        bluetoothGatt?.discoverServices()
            ?: Logger.e("Not connected to a BLE device!")
    }

    override fun readCharacteristic(serviceUuid: String, charUuid: String) {
        Logger.w("Reading Value ... ")
        bluetoothGatt?.let { gatt ->
            val characteristic = gatt
                .getService(UUID.fromString(serviceUuid))
                ?.getCharacteristic(UUID.fromString(charUuid))
            characteristic?.let { char ->
                if (char.isReadable())
                    _loading.postValue(Event(true))
                gatt.readCharacteristic(char)
            }
        } ?: Logger.e("Not connected to a BLE device!")
    }

    override fun writeCharacteristic(
        serviceUuid: String,
        charUuid: String,
        payload: ByteArray
    ) {
        Logger.w("Writing Value ... ")
        bluetoothGatt?.let { gatt ->
            val characteristic = gatt
                .getService(UUID.fromString(serviceUuid))
                ?.getCharacteristic(UUID.fromString(charUuid))
            characteristic?.let { char ->
                val writeType = when {
                    char.isWritable() ->
                        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                    char.isWritableWithoutResponse() ->
                        BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                    else -> {
                        Logger.e("Can not Write to Characteristic")
                        return
                    }
                }
                characteristic.writeType = writeType
                characteristic.value = payload
                gatt.writeCharacteristic(characteristic)
                _loading.postValue(Event(true))
            }
        } ?: Logger.e("Not connected to a BLE device!")
    }

    override fun enableNotification(serviceUuid: String, charUuid: String) {
        val cccdUuid = UUID.fromString(CCCD_UUID)
        bluetoothGatt?.let { gatt ->
            val characteristic = gatt
                .getService(UUID.fromString(serviceUuid))
                ?.getCharacteristic(UUID.fromString(charUuid))
            characteristic?.let { char ->
                val payload = when {
                    char.isIndicatable() ->
                        BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                    char.isNotifiable() ->
                        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    else -> {
                        Logger.e("Can't Enable Notification!")
                        return
                    }
                }

                Logger.w("Enabling Notification ... ")
                char.getDescriptor(cccdUuid)?.let { cccDescriptor ->
                    if (bluetoothGatt?.setCharacteristicNotification(
                            characteristic,
                            true
                        ) == false
                    ) {
                        Logger.e("Enabling Notification Failed!")
                        return
                    }
                    writeDescriptor(cccDescriptor, payload)
                } ?: Logger.e("${char.uuid}: CCCD Not Found!")
            }
        } ?: Logger.e("Not connected to a BLE device!")
    }

    override fun disableNotification(serviceUuid: String, charUuid: String) {
        val cccdUuid = UUID.fromString(CCCD_UUID)
        bluetoothGatt?.let { gatt ->
            val characteristic = gatt
                .getService(UUID.fromString(serviceUuid))
                ?.getCharacteristic(UUID.fromString(charUuid))
            characteristic?.let { char ->
                if (!char.isIndicatable() && char.isNotifiable()) {
                    Logger.e("Notification not Supported")
                }
                Logger.w("Disabling Notification ... ")
                char.getDescriptor(cccdUuid)?.let { cccDescriptor ->
                    if (bluetoothGatt?.setCharacteristicNotification(
                            characteristic,
                            true
                        ) == false
                    ) {
                        Logger.e("Disabling Notification Failed!")
                        return
                    }
                    writeDescriptor(
                        cccDescriptor,
                        BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                    )
                } ?: Logger.e("${char.uuid}: CCCD Not Found!")
            }
        } ?: Logger.e("Not connected to a BLE device!")
    }

    override fun stopScan() {
        Logger.w("Stopping Scan ... ")
        bleScanner?.stopScan(scanCallback)
    }

    override fun setBleCallbacks(
        onDeviceFound: (BleDevice) -> Unit,
        onConnectionChange: (ConnectionStatus) -> Unit,
        onServiceDiscovered: (List<BleService>) -> Unit,
        onCharacteristicRead: (BleCharacteristic) -> Unit,
        onCharacteristicChange: (BleCharacteristic) -> Unit,
        onCharacteristicWrite: (BleCharacteristic) -> Unit
    ) {
        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                _loading.postValue(Event(false))
                result?.let { scanResult ->
                    // ... Required for RSSI update
                    val indexQuery = scanResults.indexOfFirst { device ->
                        device.address == scanResult.device?.address
                    }
                    if (indexQuery != -1) {
                        scanResults[indexQuery] = scanResult.device
                    } else {
                        // ... New device found
                        Logger.i("Found device: $scanResult")
                        scanResult.device?.let { device ->
                            scanResults.add(device)
                            onDeviceFound(
                                BleDevice(
                                    name = device.name ?: "Unnamed",
                                    address = device.address ?: "Null"
                                )
                            )
                        }
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                _loading.postValue(Event(false))
                Logger.e("Scan Failed!")
            }
        }

        gattCallback = object : BluetoothGattCallback() {
            override fun onPhyUpdate(
                gatt: BluetoothGatt?,
                txPhy: Int,
                rxPhy: Int,
                status: Int
            ) {
                if (status == BluetoothGatt.GATT_SUCCESS)
                    Logger.i("Phy Update: Tx = $txPhy, Rx = $rxPhy")
            }

            override fun onPhyRead(
                gatt: BluetoothGatt?,
                txPhy: Int,
                rxPhy: Int,
                status: Int
            ) {
                if (status == BluetoothGatt.GATT_SUCCESS)
                    Logger.i("Phy Read: Tx = $txPhy, Rx = $rxPhy")
            }

            override fun onConnectionStateChange(
                gatt: BluetoothGatt?,
                status: Int,
                newState: Int
            ) {
                _loading.postValue(Event(false))
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTING -> {
                            Logger.i("Connecting ...")
                            onConnectionChange(ConnectionStatus.CONNECTING)
                        }
                        BluetoothProfile.STATE_CONNECTED -> {
                            Logger.i("Connected")
                            onConnectionChange(ConnectionStatus.CONNECTED)
                            bluetoothGatt = gatt
                        }
                        BluetoothProfile.STATE_DISCONNECTING -> {
                            Logger.i("Disconnecting ...")
                            onConnectionChange(ConnectionStatus.DISCONNECTING)
                        }
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            Logger.i("Disconnected")
                            onConnectionChange(ConnectionStatus.DISCONNECTED)
                        }
                    }
                } else {
                    Logger.e("Connection Failed!")
                }
            }

            override fun onServicesDiscovered(
                gatt: BluetoothGatt?,
                status: Int
            ) {
                _loading.postValue(Event(false))
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Logger.i("${gatt?.services?.size} Services Discovered")
                    gatt?.services?.let { serviceList ->
                        onServiceDiscovered(
                            serviceList.map { service ->
                                service.simplify()
                            }
                        )
                    }
                }
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                _loading.postValue(Event(false))
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Logger.i(
                            "Value: ${characteristic?.value?.toHexString()}"
                        )
                        characteristic?.let { char ->
                            onCharacteristicRead(char.simplify())
                        }
                    }
                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                        Logger.e("Read not Permitted!")
                    }
                    else -> Logger.e("Read Failed!")
                }
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                _loading.postValue(Event(false))
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Logger.i("Characteristic Written")
                        characteristic?.let { char ->
                            onCharacteristicWrite(char.simplify())
                        }
                    }
                    BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH -> {
                        Logger.e("Write Exceeded Connection ATT MTU!")
                    }
                    BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                        Logger.e("Write not Permitted")
                    }
                    else -> {
                        Logger.e("Characteristic Write Failed")
                    }
                }
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                _loading.postValue(Event(false))
                Logger.i("Value: ${characteristic?.value?.toHexString()}")
                characteristic?.let { char ->
                    onCharacteristicChange(char.simplify())
                }
            }

            override fun onDescriptorRead(
                gatt: BluetoothGatt?,
                descriptor: BluetoothGattDescriptor?,
                status: Int
            ) {
                super.onDescriptorRead(gatt, descriptor, status)
                if (status == BluetoothGatt.GATT_SUCCESS)
                    Logger.i("Descriptor: $descriptor")
            }

            override fun onDescriptorWrite(
                gatt: BluetoothGatt?,
                descriptor: BluetoothGattDescriptor?,
                status: Int
            ) {
                _loading.postValue(Event(false))
                if (status == BluetoothGatt.GATT_SUCCESS)
                    Logger.i("Descriptor Written")
            }

            override fun onReliableWriteCompleted(
                gatt: BluetoothGatt?,
                status: Int
            ) {
                super.onReliableWriteCompleted(gatt, status)
                if (status == BluetoothGatt.GATT_SUCCESS)
                    Logger.i("Reliable Write Complete")
            }

            override fun onReadRemoteRssi(
                gatt: BluetoothGatt?,
                rssi: Int,
                status: Int
            ) {
                super.onReadRemoteRssi(gatt, rssi, status)
                if (status == BluetoothGatt.GATT_SUCCESS)
                    Logger.i("RSSI: $rssi")
            }

            override fun onMtuChanged(
                gatt: BluetoothGatt?,
                mtu: Int,
                status: Int
            ) {
                super.onMtuChanged(gatt, mtu, status)
                if (status == BluetoothGatt.GATT_SUCCESS)
                    Logger.i("MTU Changed: $mtu")
            }

            override fun onServiceChanged(
                gatt: BluetoothGatt
            ) {
                super.onServiceChanged(gatt)
                Logger.i("Service Changed")
            }

        }
    }

    private fun writeDescriptor(
        descriptor: BluetoothGattDescriptor,
        payload: ByteArray
    ) {
        bluetoothGatt?.let { gatt ->
            descriptor.value = payload
            gatt.writeDescriptor(descriptor)
            _loading.postValue(Event(true))
        } ?: Logger.e("Not connected to a BLE device!")
    }
}