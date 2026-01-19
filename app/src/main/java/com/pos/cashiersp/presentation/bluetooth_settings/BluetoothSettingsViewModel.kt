package com.pos.cashiersp.presentation.bluetooth_settings

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.presentation.util.StateStatus
import com.pos.cashiersp.use_case.DataStoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class BluetoothDeviceInfo(
    val name: String,
    val address: String,
    val isConnected: Boolean = false,
    val isPaired: Boolean = false
)

data class BluetoothState(
    val isLoading: Boolean = false,
    val isScanning: Boolean = false,
    val devices: List<BluetoothDeviceInfo> = emptyList(),
    val connectedDevice: BluetoothDeviceInfo? = null,
    val error: String? = null
)

@HiltViewModel
class BluetoothSettingsViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase,
    @ApplicationContext private val context: Context, // Should use Controller instead and inject using hilt
) : ViewModel() {
    private val _tenantId = mutableIntStateOf(0)
    private val _storeId = mutableIntStateOf(0)

    private val _vmState = mutableStateOf(BluetoothState())
    val vmState: State<BluetoothState> = _vmState

    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private var selectedConnection: BluetoothConnection? = null

    init {
        loadData()
        loadPairedDevices()
    }

    fun onEvent(event: BluetoothSettingsEvent) {
        when (event) {

            else -> {}
        }
    }

    @SuppressLint("MissingPermission")
    fun loadPairedDevices() {
        _vmState.value = _vmState.value.copy(isLoading = true, error = null)

        try {
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
                _vmState.value = _vmState.value.copy(
                    isLoading = false,
                    error = "Bluetooth is not enabled"
                )
                return
            }

            // Get paired devices
            val pairedDevices = bluetoothAdapter.bondedDevices
            val deviceList = pairedDevices.map { device ->
                BluetoothDeviceInfo(
                    name = device.name ?: "Unknown Device",
                    address = device.address,
                    isPaired = true
                )
            }

            _vmState.value = _vmState.value.copy(
                isLoading = false,
                devices = deviceList
            )
        } catch (e: Exception) {
            _vmState.value = _vmState.value.copy(
                isLoading = false,
                error = e.message ?: "Error loading devices"
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(deviceAddress: String) {
        viewModelScope.launch {
            _vmState.value = _vmState.value.copy(isLoading = true, error = null)

            try {
                withContext(Dispatchers.IO) {
                    // Find the device
                    val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                    if (device == null) {
                        _vmState.value = _vmState.value.copy(
                            isLoading = false,
                            error = "Device not found"
                        )
                        return@withContext
                    }

                    // Create connection using DantSu library
                    val connection = BluetoothConnection(device)

                    // Test connection
                    if (connection.isConnected) {
                        selectedConnection = connection

                        // Update device list with connected status
                        val updatedDevices = _vmState.value.devices.map { deviceInfo ->
                            deviceInfo.copy(
                                isConnected = deviceInfo.address == deviceAddress
                            )
                        }

                        _vmState.value = _vmState.value.copy(
                            isLoading = false,
                            devices = updatedDevices,
                            connectedDevice = BluetoothDeviceInfo(
                                name = device.name ?: "Unknown Device",
                                address = deviceAddress,
                                isConnected = true,
                                isPaired = true
                            )
                        )
                    } else {
                        _vmState.value = _vmState.value.copy(
                            isLoading = false,
                            error = "Failed to connect to device"
                        )
                    }
                }
            } catch (e: Exception) {
                _vmState.value = _vmState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error connecting to device"
                )
            }
        }
    }

    fun disconnectDevice() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    selectedConnection?.disconnect()
                    selectedConnection = null

                    val updatedDevices = _vmState.value.devices.map { device ->
                        device.copy(isConnected = false)
                    }

                    _vmState.value = _vmState.value.copy(
                        devices = updatedDevices,
                        connectedDevice = null
                    )
                } catch (e: Exception) {
                    _vmState.value = _vmState.value.copy(
                        error = e.message ?: "Error disconnecting device"
                    )
                }
            }
        }
    }

    fun getConnection(): BluetoothConnection? = selectedConnection

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCleared() {
        super.onCleared()
        selectedConnection?.disconnect()
    }

    private fun loadData() {
        combine(
            dataStoreUseCase.getCurrentTenant(),
            dataStoreUseCase.getCurrentStore()
        ) { tenantResource, storeResource ->
            Pair(tenantResource, storeResource)
        }.onEach { (tenantResource, storeResource) ->
            when {
                tenantResource is Resource.Success && storeResource is Resource.Success -> {
                    val tenantId = tenantResource.data!!.id
                    val storeId = storeResource.data!!.id

                    _tenantId.intValue = tenantId
                    _storeId.intValue = storeId
                }

                tenantResource is Resource.Error || storeResource is Resource.Error -> {

                }

                tenantResource is Resource.Loading || storeResource is Resource.Loading -> {}
            }
        }.launchIn(viewModelScope)
    }
}