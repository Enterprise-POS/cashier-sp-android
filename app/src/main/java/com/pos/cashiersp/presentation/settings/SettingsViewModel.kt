package com.pos.cashiersp.presentation.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.use_case.DataStoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase,
) : ViewModel() {
    private val _tenantName = mutableStateOf("")
    private val _tenantId = mutableIntStateOf(0)
    private val _storeName = mutableStateOf("")

    val tenantName: State<String> = _tenantName
    val storeName: State<String> = _storeName

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadStoreAndTenantData()
    }

    // Event Handler

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnClickPrinterSettings -> onClickPrinterSettings()
            SettingsEvent.OnClickSelectTenant -> onClickSelectTenant()
            is SettingsEvent.OnClickSelectStore -> onClickSelectStore(event)
        }
    }

    private fun onClickSelectTenant() {
        this.viewModelScope.launch {
            _uiEvent.emit(SettingsViewModel.UIEvent.NavigateToSelectTenant)
        }
    }

    private fun onClickPrinterSettings() {
        this.viewModelScope.launch {
            _uiEvent.emit(SettingsViewModel.UIEvent.NavigateToBluetoothSettings)
        }
    }

    private fun onClickSelectStore(event: SettingsEvent.OnClickSelectStore) {
        this.viewModelScope.launch {
            _uiEvent.emit(SettingsViewModel.UIEvent.NavigateToSelectStore(_tenantId.intValue, _tenantName.value))
        }
    }

    private fun loadStoreAndTenantData() {
        combine(
            dataStoreUseCase.getCurrentTenant(),
            dataStoreUseCase.getCurrentStore()
        ) { tenant, store -> Pair(tenant, store) }
            .onEach { (tenantResource, storeResource) ->
                when {
                    tenantResource is Resource.Success && storeResource is Resource.Success -> {
                        val tenantName = tenantResource.data!!.name
                        val tenantId = tenantResource.data.id
                        val storeName = storeResource.data!!.name
                        _tenantName.value = tenantName
                        _tenantId.intValue = tenantId
                        _storeName.value = storeName
                    }

                    tenantResource is Resource.Error || storeResource is Resource.Error -> {
                        _uiEvent.emit(
                            UIEvent.ErrorAndMustNavigateToSelectTenantScreen(
                                "Fatal Error while getting cashier data."
                            )
                        )
                    }

                    else -> { /* Loading — do nothing */
                        _storeName.value = "Error. Can not get current name"
                        _tenantName.value = "Error. Can not get tenant name"
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    sealed class UIEvent {
        data class ErrorAndMustNavigateToSelectTenantScreen(val message: String) : UIEvent()
        object NavigateToBluetoothSettings : UIEvent()
        data class NavigateToSelectStore(val tenantId: Int, val tenantName: String) : UIEvent()
        object NavigateToSelectTenant : UIEvent()
    }
}