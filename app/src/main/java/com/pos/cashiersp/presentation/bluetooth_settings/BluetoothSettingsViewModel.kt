package com.pos.cashiersp.presentation.bluetooth_settings

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.controller.BluetoothController
import com.pos.cashiersp.model.domain.BluetoothDeviceDomain
import com.pos.cashiersp.presentation.util.BluetoothUIState
import com.pos.cashiersp.presentation.util.ConnectionResult
import com.pos.cashiersp.use_case.DataStoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BluetoothSettingsViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase,
    @ApplicationContext private val context: Context, // Should use Controller instead and inject using hilt,
    private val bluetoothController: BluetoothController
) : ViewModel() {
    private val _tenantId = mutableIntStateOf(0)
    private val _storeId = mutableIntStateOf(0)

    private val _state = MutableStateFlow(BluetoothUIState())
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private var deviceConnectionJob: Job? = null


    /////////////////
    // Functionality
    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }

    fun connectToDevice(device: BluetoothDeviceDomain) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .connectToDevice(device)
            .listen()
    }

    fun onEvent(event: BluetoothSettingsEvent) {
        when (event) {

            else -> {}
        }
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

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnecting = false,
                            errorMessage = null
                        )
                    }
                }

                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnecting = false,
                            errorMessage = result.message
                        )
                    }
                }

                ConnectionResult.Connecting -> {
                    println("Connecting to device")
                }
            }
        }
            .catch { throwable ->
                bluetoothController.closeConnection()
                _state.update {
                    it.copy(
                        isConnected = false,
                        isConnecting = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}