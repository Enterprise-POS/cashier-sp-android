package com.pos.cashiersp.presentation.select_store

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.common.error
import com.pos.cashiersp.model.domain.Store
import com.pos.cashiersp.model.dto.toDomain
import com.pos.cashiersp.presentation.util.StateStatus
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.StoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SelectStoreViewModel @Inject constructor(
    private val storeUseCase: StoreUseCase,
    private val datastoreUseCase: DataStoreUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = mutableStateOf(StateStatus())
    val state: State<StateStatus> = _state
    private val _openErrorAlert = mutableStateOf(false)
    val openErrorAlert: State<Boolean> = _openErrorAlert

    private val _loadingSaveState = mutableStateOf(StateStatus())
    val loadingSaveState: State<StateStatus> = _loadingSaveState

    private val _storeList = mutableStateOf<List<Store>>(listOf())
    val storeList: State<List<Store>> = _storeList

    private val _selectedStore = mutableStateOf<Store?>(null)
    val selectedStore: State<Store?> = _selectedStore

    private val tenantId: Int = savedStateHandle.get<Int>("tenantId") ?: -1

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        this.fetchStoreList(tenantId, 1, 10, false)
    }

    fun onEvent(event: SelectStoreEvent) {
        when (event) {
            is SelectStoreEvent.OnSelectStoreCard -> {
                _selectedStore.value = event.selectedStore
            }

            is SelectStoreEvent.OnBackButtonClicked -> {
                viewModelScope.async { _uiEvent.emit(UIEvent.BackToSelectTenant) }
            }

            is SelectStoreEvent.OnCloseErrorAlert -> _openErrorAlert.value = false

            is SelectStoreEvent.OnContinueButtonClicked -> {
                val store = _selectedStore.value
                if (store == null) {
                    _loadingSaveState.value = StateStatus(error = "Error ! Please select store before continue !")
                    return
                }
                datastoreUseCase.saveSelectedStore(store.id, store.name).onEach { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            _loadingSaveState.value =
                                StateStatus(error = "Could not continue ! Something went wrong while continuing selected store")
                        }

                        is Resource.Loading -> {
                            _loadingSaveState.value = StateStatus(isLoading = true)
                        }

                        is Resource.Success -> {
                            _uiEvent.emit(UIEvent.ContinueToCashier)
                            _loadingSaveState.value = StateStatus()
                        }
                    }
                }.launchIn(viewModelScope)
            }

            is SelectStoreEvent.OnTryAgainFetchStoreList -> this.fetchStoreList(tenantId, 1, 10, false)
        }
    }

    private fun fetchStoreList(tenantId: Int, page: Int, limit: Int, includeNonActive: Boolean): Job {
        return storeUseCase.getAll(tenantId, page, limit, includeNonActive).onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    _openErrorAlert.value = true
                    _state.value = StateStatus(error = resource.message)
                }

                is Resource.Loading -> {
                    _openErrorAlert.value = false
                    _state.value = StateStatus(isLoading = true)
                }

                is Resource.Success -> {
                    val storeDto = resource.data
                    // We can't combine json class model into application so transform
                    // into pure domain application model
                    if (storeDto == null) {
                        _state.value = StateStatus(error = "Fatal Error, Request success but found nothing !")
                        return@onEach
                    }

                    _storeList.value = storeDto.stores.map { it.toDomain() }

                    // After get all the data, reset viewmodel status
                    _openErrorAlert.value = false
                    _state.value = StateStatus()
                }
            }
        }.launchIn(viewModelScope)
    }

    sealed class UIEvent {
        object BackToSelectTenant : UIEvent()
        object ContinueToCashier : UIEvent()
    }
}