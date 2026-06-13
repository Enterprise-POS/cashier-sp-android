package com.pos.cashiersp.presentation.transaction_history

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.controller.BluetoothController
import com.pos.cashiersp.model.dto.DateFilter
import com.pos.cashiersp.model.dto.QueryFilter
import com.pos.cashiersp.model.dto.SearchTransactionsDto
import com.pos.cashiersp.model.dto.toDomain
import com.pos.cashiersp.model.dto.toReceiptLine
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialogStatus
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryEvent.*
import com.pos.cashiersp.presentation.transaction_history.TransactionHistoryViewModel.UIEvent.*
import com.pos.cashiersp.presentation.util.BluetoothUIState
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.OrderItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase,
    private val orderItemUseCase: OrderItemUseCase,
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private val _tenantId = mutableIntStateOf(0)
    private val _storeId = mutableIntStateOf(0)
    private val _storeName = mutableStateOf("")
    val storeName: State<String> = _storeName

    // Store custom date range
    private val _startCalendar = mutableStateOf<Calendar?>(null)
    val startCalendar: State<Calendar?> = _startCalendar
    private val _endCalendar = mutableStateOf<Calendar?>(null)
    val endCalendar: State<Calendar?> = _endCalendar

    private val _selectedPeriod = mutableStateOf<PeriodFilter>(PeriodFilter.TODAY)
    val selectedPeriod: State<PeriodFilter> = _selectedPeriod

    private val _dateRangePicker = mutableStateOf(false)
    val dateRangePicker: State<Boolean> = _dateRangePicker
    private val _startTimePicker = mutableStateOf(false)
    val startTimePicker: State<Boolean> = _startTimePicker
    private val _endTimePicker = mutableStateOf(false)
    val endTimePicker: State<Boolean> = _endTimePicker

    private val _showSortMenu = mutableStateOf(false)
    val showSortMenu: State<Boolean> = _showSortMenu
    private val _selectedSort = mutableStateOf(SortDirection.DESC)
    val selectedSort: State<SortDirection> = _selectedSort

    private val _showColumnMenu = mutableStateOf(false)
    val showColumnMenu: State<Boolean> = _showColumnMenu
    private val _selectedColumn = mutableStateOf(ColumnName.CREATED_AT)
    val selectedColumn: State<ColumnName> = _selectedColumn

    // Floating action button and invoice search modal
    private val _showSearchInvoiceModal = mutableStateOf(false)
    val showSearchInvoiceModal: State<Boolean> = _showSearchInvoiceModal
    private val _inputInvoiceId = mutableIntStateOf(0)
    val inputInvoiceId: State<Int> = _inputInvoiceId

    private val _generalAlertDialogStatus = mutableStateOf(GeneralAlertDialogStatus())
    val generalAlertDialogStatus: State<GeneralAlertDialogStatus> = _generalAlertDialogStatus
    private val _isRequesting = mutableStateOf(false)
    val isRequesting: State<Boolean> = _isRequesting

    private val _searchTransactionsDto: MutableState<SearchTransactionsDto?> = mutableStateOf(null)
    val searchTransactionsDto: State<SearchTransactionsDto?> = _searchTransactionsDto

    private val _itemsPerPage = mutableStateOf(ItemsPerPage.TEN)
    val itemsPerPage: State<ItemsPerPage> = _itemsPerPage

    private val _uiEvent = MutableSharedFlow<TransactionHistoryViewModel.UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // Transaction History screen only print for connected only printers
    private val _isPrinting = mutableStateOf(false)

    init {
        loadDataStoreData()
        // This automatically set the default filter as today when the transaction history page open
        this.onEvent(TransactionHistoryEvent.OnChangePeriodFilter(PeriodFilter.TODAY))
    }

    fun onEvent(event: TransactionHistoryEvent) {
        when (event) {
            is SetStartCalendar -> {
                if (_startCalendar.value == null) return
                val calendar = _startCalendar.value!!.clone() as Calendar
                event.pairOfCalendarSetters.forEach { (field, value) ->
                    calendar.set(field, value)
                }
                _startCalendar.value = calendar
            }

            is SetEndCalendar -> {
                if (_endCalendar.value == null) return
                val calendar = _endCalendar.value!!.clone() as Calendar
                event.pairOfCalendarSetters.forEach { (field, value) ->
                    calendar.set(field, value)
                }
                _endCalendar.value = calendar
            }

            is OnChangeStartCalendar -> _startCalendar.value = event.startCalendar
            is OnChangeEndCalendar -> _endCalendar.value = event.endCalendar

            is OnChangePeriodFilter -> {
                _selectedPeriod.value = event.selectedPeriodFilter
                if (event.selectedPeriodFilter == PeriodFilter.CUSTOM) return

                val now = Calendar.getInstance()
                val start = now.clone() as Calendar
                val end = now.clone() as Calendar
                when (event.selectedPeriodFilter) {
                    PeriodFilter.TODAY -> {
                        start.set(Calendar.HOUR_OF_DAY, 0)
                        start.set(Calendar.MINUTE, 0)
                        start.set(Calendar.SECOND, 0)
                        start.set(Calendar.MILLISECOND, 0)

                        end.set(Calendar.HOUR_OF_DAY, 23)
                        end.set(Calendar.MINUTE, 59)
                        end.set(Calendar.SECOND, 0)
                        end.set(Calendar.MILLISECOND, 0)
                    }

                    PeriodFilter.LAST_HOUR -> {
                        start.add(Calendar.HOUR_OF_DAY, -1)
                    }

                    PeriodFilter.LAST_6_HOUR -> {
                        start.add(Calendar.HOUR_OF_DAY, -6)
                    }

                    PeriodFilter.LAST_12_HOUR -> {
                        start.add(Calendar.HOUR_OF_DAY, -12)
                    }

                    PeriodFilter.LAST_7_DAYS -> {
                        start.add(Calendar.DAY_OF_YEAR, -7)
                    }

                    PeriodFilter.THIS_MONTH -> {
                        start.set(Calendar.DAY_OF_MONTH, 1)
                        start.set(Calendar.HOUR_OF_DAY, 0)
                        start.set(Calendar.MINUTE, 0)
                        start.set(Calendar.SECOND, 0)
                        start.set(Calendar.MILLISECOND, 0)
                    }

                    PeriodFilter.CUSTOM -> throw Exception("Unexpected behavior from TransactionHistoryEvent.OnChangePeriodFilter")
                }

                _startCalendar.value = start
                _endCalendar.value = end
            }

            is PickersInput -> {
                when (event.selectPicker) {
                    Pickers.DATE_RANGE_PICKER -> _dateRangePicker.value = event.setInto
                    Pickers.START_TIME_PICKER -> _startTimePicker.value = event.setInto
                    Pickers.END_TIME_PICKER -> _endTimePicker.value = event.setInto
                }
            }

            is OnClickColumnsDropDown -> _showColumnMenu.value = event.setInto
            is OnPickColumnsDropDown -> _selectedColumn.value = event.columnName

            is OnClickSortersDropDown -> _showSortMenu.value = event.setInto
            is OnPickSortersDropDown -> _selectedSort.value = event.sortDirection

            OnClickShowReportBtn -> {
                if (_startCalendar.value == null || _endCalendar.value == null) return

                val isRequesting = _isRequesting.value
                if (isRequesting) return
                _isRequesting.value = true
                val ascending = _selectedSort.value == SortDirection.ASC

                requestReport(
                    startCalendar = _startCalendar.value!!,
                    endCalendar = _endCalendar.value!!,
                    page = 1, // Everytime user click report btn, the page always reset from 1
                    limit = _itemsPerPage.value.value,
                    ascending = ascending,
                    columnName = _selectedColumn.value,
                    storeId = _storeId.intValue,
                    tenantId = _tenantId.intValue
                )
            }

            is OnPageChange -> {
                val currentDto = _searchTransactionsDto.value
                if (currentDto == null) return

                val goToPage = event.page
                if (currentDto.page == goToPage) return

                val isRequesting = _isRequesting.value
                if (isRequesting) return
                _isRequesting.value = true

                // Count how many page will be
                val totalPages = ceil(currentDto.totalCount.toDouble() / currentDto.limit.toDouble()).toInt()
                if (event.page < 0 || event.page > totalPages) return

                val limit = _itemsPerPage.value.value
                val page = event.page
                val ascending = _selectedSort.value == SortDirection.ASC
                // isLoading value will be auto false by requestReport function
                requestReport(
                    startCalendar = _startCalendar.value!!,
                    endCalendar = _endCalendar.value!!,
                    page = page,
                    limit = limit,
                    ascending = ascending,
                    columnName = _selectedColumn.value,
                    storeId = _storeId.intValue,
                    tenantId = _tenantId.intValue
                )
            }

            is OnClickItemsPerPage -> {
                _itemsPerPage.value = event.value
            }

            OnCloseGeneralDialog ->
                _generalAlertDialogStatus.value = GeneralAlertDialogStatus()

            OnClickResetBtn -> {
                this.onEvent(OnChangePeriodFilter(PeriodFilter.TODAY))
                this.onEvent(OnPickColumnsDropDown(ColumnName.CREATED_AT))
                this.onEvent(OnPickSortersDropDown(SortDirection.DESC))
            }

            is OnLongPressedAndClickPrint -> {
                val connectedDevices = bluetoothController.pairedDevices.value

                if (connectedDevices.isEmpty()) {
                    _generalAlertDialogStatus.value =
                        GeneralAlertDialogStatus.error("Print Error", "No printer connected. Please check you devices")
                    return
                }

                if (_isPrinting.value) return
                _isPrinting.value = true


                // Tell user we are printing the receipt by AlertDialog
                _generalAlertDialogStatus.value = GeneralAlertDialogStatus.loading("Printing...")

                orderItemUseCase.findTransactionsById(event.id, _tenantId.intValue).onEach { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            _generalAlertDialogStatus.value =
                                GeneralAlertDialogStatus.error(
                                    "Print error",
                                    resource.message ?: "Unexpected error from findTransactionsById"
                                )
                            _isPrinting.value = false
                        }

                        is Resource.Loading -> {}

                        is Resource.Success -> {
                            // Start printing
                            // println(resource.data!!)
                            val orderItem = resource.data!!.orderItem.toDomain()
                            val purchasedItemList = resource.data.purchasedItemList.map { it.toReceiptLine() }
                            bluetoothController.printReceipt(orderItem = orderItem, purchasedItems = purchasedItemList)

                            _isPrinting.value = false
                            _generalAlertDialogStatus.value = GeneralAlertDialogStatus()
                        }
                    }
                }.launchIn(viewModelScope)
            }

            is OnTapSaleCard -> {
                val id = event.orderItemId
                viewModelScope.launch {
                    _uiEvent.emit(GotoInvoiceDetailScreen(id))
                }
            }

            OnTapFloatingActionBtn -> _showSearchInvoiceModal.value = true
            OnClickCancelAtInvoiceSearchModal -> {
                _inputInvoiceId.intValue = 0
                _showSearchInvoiceModal.value = false
            }

            is OnChangeInvoiceSearchInput -> {
                val inp = event.inp

                if (inp.isEmpty()) {
                    _inputInvoiceId.intValue = 0
                    return
                }

                if (!inp.all { it.isDigit() }) return

                _inputInvoiceId.intValue = inp.toInt()
            }

            OnClickConfirmAtInvoiceSearchModal -> {
                if (_inputInvoiceId.intValue <= 0) return
                viewModelScope.launch {
                    _uiEvent.emit(UIEvent.GotoInvoiceDetailScreen(_inputInvoiceId.intValue))

                    _inputInvoiceId.intValue = 0
                    _showSearchInvoiceModal.value = false
                }
            }
        }
    }

    private fun loadDataStoreData() {
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
                    _storeName.value = storeResource.data.name
                }

                tenantResource is Resource.Error || storeResource is Resource.Error -> {
                    // Navigate to login
                }

                tenantResource is Resource.Loading || storeResource is Resource.Loading -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun requestReport(
        startCalendar: Calendar,
        endCalendar: Calendar,
        page: Int,
        limit: Int,
        ascending: Boolean,
        columnName: ColumnName,
        storeId: Int,
        tenantId: Int
    ) {
        // _isRequesting will auto false by this method
        orderItemUseCase.searchTransactions(
            storeId = storeId,
            tenantId = tenantId,
            page = page,
            limit = limit,
            dateFilter = DateFilter(
                columnName.dbname,
                (startCalendar.timeInMillis / 1000.0).toInt(),
                (endCalendar.timeInMillis / 1000.0).toInt()
            ),
            queryFilter = QueryFilter(columnName.dbname, ascending)
        ).onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    _isRequesting.value = false
                    _generalAlertDialogStatus.value =
                        GeneralAlertDialogStatus.error("Request report error", resource.message!!)
                }

                is Resource.Loading -> {}

                is Resource.Success -> {
                    _isRequesting.value = false
                    _searchTransactionsDto.value = resource.data
                }
            }
        }.launchIn(viewModelScope)
    }

    sealed class UIEvent {
        data class GotoInvoiceDetailScreen(val orderItemId: Int) : UIEvent()
    }
}

enum class PeriodFilter(val label: String) {
    TODAY("Today"),
    LAST_HOUR("Last hour"),
    LAST_6_HOUR("Last 6 hours"),
    LAST_12_HOUR("Last 12 hours"),
    LAST_7_DAYS("Last 7 days"),
    THIS_MONTH("This month"),
    CUSTOM("Custom")
}

enum class Pickers {
    DATE_RANGE_PICKER,
    START_TIME_PICKER,
    END_TIME_PICKER,
}

enum class SortDirection(val label: String, val valueLabel: String) {
    ASC("Oldest First", "Lowest"),
    DESC("Latest First", "Highest")
}

enum class ColumnName(val label: String, val dbname: String) {
    CREATED_AT("Created At", "created_at"),
    AMOUNT("Total Amount", "total_amount")
}

enum class ItemsPerPage(val value: Int) {
    TEN(10),
    TWENTY(20),
    THIRTY(30)
}