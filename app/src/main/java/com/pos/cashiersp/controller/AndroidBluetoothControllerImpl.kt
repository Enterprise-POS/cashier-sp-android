package com.pos.cashiersp.controller

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import java.io.IOException
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
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices) devices else devices + newDevice
        }
    }

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
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        bluetoothAdapter?.cancelDiscovery()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        TODO("Not yet implemented")
    }

    override fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult> {
        return flow {
            println("Hellow 1")
            /*
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }
            * */

            val remoteDevice =
                bluetoothAdapter
                    ?: throw IllegalStateException("Bluetooth adapter not available")

            bluetoothAdapter?.cancelDiscovery()

            val btDevice = remoteDevice.getRemoteDevice(device.address)
            println("Hellow2")

            // Wait until bonded
            if (btDevice.bondState != android.bluetooth.BluetoothDevice.BOND_BONDED) {
                btDevice.createBond()
                waitForBond(btDevice)
            }
            _scannedDevices.update { it - device }
            _pairedDevices.update { it + device }
            emit(ConnectionResult.ConnectionEstablished)
        }.flowOn(Dispatchers.IO)
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