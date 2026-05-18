package com.pos.cashiersp.presentation.stock_management

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.domain.StoreStock
import com.pos.cashiersp.model.dto.StoreStockV2
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialogStatus
import com.pos.cashiersp.presentation.stock_management.StockManagementViewModel.UIBottomSheet.*
import com.pos.cashiersp.presentation.util.StateStatus
import com.pos.cashiersp.repository.StoreStockRepository
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.StoreStockUseCase
import com.pos.cashiersp.use_case.StoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class StockManagementViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase,
    private val storeStockUseCase: StoreStockUseCase,
) : ViewModel() {
    private val _tenantId = mutableIntStateOf(0)
    private val _storeId = mutableIntStateOf(0)
    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    private val _uiBottomSheet = MutableSharedFlow<UIBottomSheet>()
    val uiBottomSheet = _uiBottomSheet.asSharedFlow()

    private val _itemsTotal = mutableIntStateOf(0)
    val itemsTotal: State<Int> = _itemsTotal
    private val _storeStocks = mutableStateOf<List<StoreStockV2>>(listOf())
    val storeStocks: State<List<StoreStockV2>> = _storeStocks

    private val _page = mutableIntStateOf(1)
    val page: State<Int> = _page
    private val _itemsPerPage = mutableStateOf(ItemsPerPage.TEN) // Using local ItemsPerPage, see most bottom below
    val itemsPerPage: State<ItemsPerPage> = _itemsPerPage

    private val _generalAlertDialogStatus = mutableStateOf(GeneralAlertDialogStatus())
    val generalAlertDialogStatus: State<GeneralAlertDialogStatus> = _generalAlertDialogStatus

    private val _openDetailStockDialog = mutableStateOf(GeneralAlertDialogStatus())
    val openDetailStockDialog: State<GeneralAlertDialogStatus> = _openDetailStockDialog
    private val _selectedDetailStockDialog = mutableStateOf<StoreStockV2?>(null)
    val selectedDetailStockDialog: State<StoreStockV2?> = _selectedDetailStockDialog

    private val _requestingState = mutableStateOf(StateStatus(isLoading = true)) // Requesting first page by default
    val requestingState: State<StateStatus> = _requestingState

    // By placing the state here, any saved / changed stated by SimpleTextField will also captured by viewmodel
    val searchTextFieldState = TextFieldState()

    init {
        // If user is logged in then _tenantId and _storeId will be automatically reassign
        loadTenantAndStoreId()
    }

    fun onEvent(event: StockManagementEvent) {
        when (event) {
            StockManagementEvent.OnTapNextPageButton -> {
                val toPage = _page.intValue + 1
                val totalPages = ceil(_itemsTotal.intValue.toDouble() / _itemsPerPage.value.value.toDouble()).toInt()
                if (toPage <= 0 || toPage > totalPages) return

                _requestingState.value = StateStatus(isLoading = true)
                val previousPage = _page.intValue
                _page.intValue = toPage

                getStoreStockV2(
                    _tenantId.intValue, _storeId.intValue, toPage, _itemsPerPage.value.value,
                    onError = { _page.intValue = previousPage }
                )
            }

            StockManagementEvent.OnTapPrevPageButton -> {
                val toPage = _page.intValue - 1
                val totalPages = ceil(_itemsTotal.intValue.toDouble() / _itemsPerPage.value.value.toDouble()).toInt()
                if (toPage <= 0 || toPage > totalPages) return

                _requestingState.value = StateStatus(isLoading = true)
                val previousPage = _page.intValue
                _page.intValue = toPage

                getStoreStockV2(
                    _tenantId.intValue, _storeId.intValue, toPage, _itemsPerPage.value.value,
                    onError = { _page.intValue = previousPage }
                )
            }

            is StockManagementEvent.OnTapPaginationPageButton -> {
                val toPage = event.toPage
                val totalPages = ceil(_itemsTotal.intValue.toDouble() / _itemsPerPage.value.value.toDouble()).toInt()
                if (toPage <= 0 || toPage > totalPages) return

                _requestingState.value = StateStatus(isLoading = true)
                val previousPage = _page.intValue
                _page.intValue = toPage

                getStoreStockV2(
                    _tenantId.intValue, _storeId.intValue, toPage, _itemsPerPage.value.value,
                    onError = { _page.intValue = previousPage }
                )
            }

            StockManagementEvent.OnRefreshItemCatalogButton -> {
                val searchValue = searchTextFieldState.text.toString()
                val tenantId = _tenantId.intValue
                val storeId = _storeId.intValue
                val page = _page.intValue
                val limit = _itemsPerPage.value
                _requestingState.value = _requestingState.value.copy(isLoading = true)
                getStoreStockV2(tenantId, storeId, page, limit.value, searchValue)
            }

            StockManagementEvent.OnCloseGeneralDialog -> {
                _generalAlertDialogStatus.value = GeneralAlertDialogStatus()
            }

            is StockManagementEvent.OnTapViewDetailsDropDown -> {
                _selectedDetailStockDialog.value = event.selectedItem
                _openDetailStockDialog.value = _openDetailStockDialog.value.copy(showDialog = true)
                viewModelScope.launch {
                    _uiBottomSheet.emit(ShowModalBottomSheet(true))
                }
            }

            StockManagementEvent.OnTapCloseDetailsBottomSheet -> {
                _openDetailStockDialog.value = _openDetailStockDialog.value.copy(showDialog = false)
                // Using UI event otherwise the animation will not correctly show
                viewModelScope.launch {
                    _uiBottomSheet.emit(ShowModalBottomSheet(false))
                }
            }

            StockManagementEvent.OnClearSearchProduct -> {
                // Reset the condition
                _requestingState.value = _requestingState.value.copy(isLoading = true)
                getStoreStockV2(_tenantId.intValue, _storeId.intValue, _page.intValue, _itemsPerPage.value.value)
            }

            is StockManagementEvent.OnSearchProduct -> {
                val searchValue = event.text
                val tenantId = _tenantId.intValue
                val storeId = _storeId.intValue
                val page = 1
                val limit = _itemsPerPage.value

                _requestingState.value = _requestingState.value.copy(isLoading = true)
                getStoreStockV2(
                    tenantId, storeId, page, limit.value, searchValue,
                    onFinish = { _page.intValue = 1 },
                )
            }
        }
    }

    private fun getStoreStockV2(
        tenantId: Int,
        storeId: Int,
        page: Int,
        limit: Int,
        nameQuery: String = "",
        onFinish: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        storeStockUseCase.getV2(tenantId, storeId, page, limit, nameQuery).onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    _requestingState.value = StateStatus(error = resource.message)
                    _generalAlertDialogStatus.value =
                        GeneralAlertDialogStatus.error("Request Failed", resource.message!!)

                    onError()
                }

                is Resource.Loading -> {}

                is Resource.Success -> {
                    _requestingState.value = StateStatus()
                    val data = resource.data!!
                    _itemsTotal.intValue = data.count
                    _storeStocks.value = data.storeStocks

                    onFinish()
                }
            }
        }.launchIn(viewModelScope)
    }

    // Only called once
    private fun onTenantAndStoreReady(tenantId: Int, storeId: Int) {
        getStoreStockV2(tenantId, storeId, _page.intValue, _itemsPerPage.value.value)
    }

    private fun loadTenantAndStoreId() {
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

                    this.onTenantAndStoreReady(tenantId, storeId)
                }

                tenantResource is Resource.Error || storeResource is Resource.Error -> {
                    _uiEvent.emit(UIEvent.ErrorAndMustNavigateToSelectTenantScreen("Fatal Error while get cashier data."))
                }

                tenantResource is Resource.Loading || storeResource is Resource.Loading -> {}
            }
        }.launchIn(viewModelScope)
    }

    sealed class UIEvent {
        data class ErrorAndMustNavigateToSelectTenantScreen(val message: String) : UIEvent()
    }

    sealed class UIBottomSheet {
        data class ShowModalBottomSheet(val show: Boolean) : UIBottomSheet()
    }
}

enum class ItemsPerPage(val value: Int) {
    FIVE(5),
    TEN(10),
    TWENTY(20),
    THIRTY(30)
}