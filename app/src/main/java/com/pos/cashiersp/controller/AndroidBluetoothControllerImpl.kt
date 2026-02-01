package com.pos.cashiersp.controller

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.pos.cashiersp.model.domain.BluetoothDevice
import com.pos.cashiersp.model.domain.BluetoothDeviceDomain
import com.pos.cashiersp.model.domain.toBluetoothDeviceDomain
import com.pos.cashiersp.presentation.util.ConnectionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import java.util.UUID

@SuppressLint("MissingPermission")
class AndroidBluetoothControllerImpl(
    private val context: Context,
) : BluetoothController {
    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    // get own address, bluetooth name, get list of scan devices, get list of pair devices
    // to initiate scan, to initiate connection, to start server
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver { device: android.bluetooth.BluetoothDevice ->
        _scannedDevices.update { devices: List<BluetoothDeviceDomain> ->
            // Must check both for _scannedDevices and _pairedDevices otherwise double device might appear
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices || newDevice in _pairedDevices.value) devices else devices + newDevice
        }
    }

    private val _isPrinting = MutableStateFlow(false)
    val isPrinting: StateFlow<Boolean> get() = _isPrinting.asStateFlow()

    init {
        updatePairedDevices()
    }

    override fun startDiscovery() {
        /**
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {return
        }
         */

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND)
        )

        updatePairedDevices()

        bluetoothAdapter?.startDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
    }

    private fun updatePairedDevices() {
        /*
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
        return
        }
         */
        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    override fun stopDiscovery() {
        /*
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        * */

        bluetoothAdapter?.cancelDiscovery()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        TODO("Not yet implemented")
    }

    override fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult> {
        return flow {
            /*
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }
            * */

            emit(ConnectionResult.Connecting)
            val remoteDevice =
                bluetoothAdapter
                    ?: throw IllegalStateException("Bluetooth adapter not available")

            bluetoothAdapter?.cancelDiscovery()

            val btDevice = remoteDevice.getRemoteDevice(device.address)

            // Wait until bonded
            if (btDevice.bondState != android.bluetooth.BluetoothDevice.BOND_BONDED) {
                btDevice.createBond()
                waitForBond(btDevice)
            }

            // Is it need some socket ?

            // Update the scanned and paired. otherwise the UI crash because the key at UI must unique
            _scannedDevices.update { it - device }
            _pairedDevices.update { it + device }
            emit(ConnectionResult.ConnectionEstablished)
        }.catch {
            emit(ConnectionResult.Error(it.message ?: "Unknown error occurred"))
        }.flowOn(Dispatchers.IO)
    }

    /*
        We don't pass the value to function because interface already now which printer by default should print
        bluetoothController.withConnectedDevicesPrintReceipt(_state.value.pairedDevices)
    * */
    override fun withConnectedDevicesPrintReceipt(devices: List<BluetoothDevice>) {
        _isPrinting.update { true }
        devices.forEach { device ->
            try {
                val androidDevice = bluetoothAdapter?.getRemoteDevice(device.address)
                val connection = BluetoothConnection(androidDevice)

                val printer = EscPosPrinter(connection, 183, 76.5f, 48)
                val formattedText = createFormattedText(printer)

                // Important: Give time for print job to complete
                // delay(2000) // 2 seconds delay between printers

                printer.printFormattedTextAndCut(formattedText)

                // Close connection explicitly if library supports it
                // connection.disconnect()

            } catch (e: Exception) {
                println("Printer Failed to print to ${device.name}: ${e.message}")
                // Continue to next printer even if one fails
            }
        }
        _isPrinting.update { false }
    }

    suspend fun waitForBond(device: android.bluetooth.BluetoothDevice) {
        while (device.bondState != android.bluetooth.BluetoothDevice.BOND_BONDED) {
            delay(300)
        }
    }

    override fun closeConnection() {
        TODO("Not yet implemented")
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private fun createFormattedText(printer: EscPosPrinter): String {
        return "[C]<font size='big'>TASTY BITES</font>\n" +
                "[C]Order #123 | Table 5\n" +
                "[C]================================\n" +
                "[L]<b>Burger Deluxe</b>          12.99\n" +
                "[L]  - No onions\n" +
                "[L]  - Extra cheese\n" +
                "[L]\n" +
                "[L]<b>Fries (Large)</b>           4.99\n" +
                "[L]<b>Coke</b>                    2.50\n" +
                "[L]  x2\n" +
                "[C]--------------------------------\n" +
                "[R]SUBTOTAL:                  20.48\n" +
                "[R]TIP (18%):                  3.69\n" +
                "[C]================================\n" +
                "[R]<b>TOTAL:                    24.17</b>\n"
        /*
        return "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
            printer,
            context.resources.getDrawableForDensity(
                R.drawable.logo, DisplayMetrics.DENSITY_MEDIUM
            )
        ) + "</img>\n" +
                "[L]\n" +
                "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                "[L]\n" +
                "[C]================================\n" +
                "[L]\n" +
                "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                "[L]  + Size : S\n" +
                "[L]\n" +
                "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                "[L]  + Size : 57/58\n" +
                "[L]\n" +
                "[C]--------------------------------\n" +
                "[R]TOTAL PRICE :[R]34.98e\n" +
                "[R]TAX :[R]4.23e\n" +
                "[L]\n" +
                "[C]================================\n" +
                "[L]\n" +
                "[L]<font size='tall'>Customer :</font>\n" +
                "[L]Raymond DUPONT\n" +
                "[L]5 rue des girafes\n" +
                "[L]31547 PERPETES\n" +
                "[L]Tel : +33801201456\n" +
                "[L]\n" +
                "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>"
                "[L]\n"
    }
         */
        return "test"
    }
}

// This is the broadcast class where our android device will received the data that we need
class FoundDeviceReceiver(
    private val onDeviceFound: (android.bluetooth.BluetoothDevice) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            android.bluetooth.BluetoothDevice.ACTION_FOUND -> {
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        android.bluetooth.BluetoothDevice.EXTRA_DEVICE,
                        android.bluetooth.BluetoothDevice::class.java
                    )
                } else {
                    intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE)
                }

                // Will execute the onDeviceFound callback that we give at AndroidBluetoothControllerImpl
                device?.let(onDeviceFound)
            }
        }
    }
}