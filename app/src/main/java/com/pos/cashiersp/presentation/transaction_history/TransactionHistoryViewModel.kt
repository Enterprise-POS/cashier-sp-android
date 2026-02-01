package com.pos.cashiersp.presentation.transaction_history

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.DateFilter
import com.pos.cashiersp.model.dto.QueryFilter
import com.pos.cashiersp.model.dto.SearchTransactionsDto
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialogStatus
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.OrderItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase,
    private val orderItemUseCase: OrderItemUseCase,
) : ViewModel() {

    private val _tenantId = mutableIntStateOf(0)
    private val _storeId = mutableIntStateOf(0)

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
    private val _selectedSort = mutableStateOf(SortDirection.ASC)
    val selectedSort: State<SortDirection> = _selectedSort

    private val _showColumnMenu = mutableStateOf(false)
    val showColumnMenu: State<Boolean> = _showColumnMenu
    private val _selectedColumn = mutableStateOf(ColumnName.CREATED_AT)
    val selectedColumn: State<ColumnName> = _selectedColumn

    private val _generalAlertDialogStatus = mutableStateOf(GeneralAlertDialogStatus())
    val generalAlertDialogStatus: State<GeneralAlertDialogStatus> = _generalAlertDialogStatus
    private val _isRequesting = mutableStateOf(false)
    val isRequesting: State<Boolean> = _isRequesting

    private val _searchTransactionsDto: MutableState<SearchTransactionsDto?> = mutableStateOf(null)
    val searchTransactionsDto: State<SearchTransactionsDto?> = _searchTransactionsDto

    private val _itemsPerPage = mutableStateOf(ItemsPerPage.TEN)
    val itemsPerPage: State<ItemsPerPage> = _itemsPerPage

    init {
        loadDataStoreData()
    }

    fun onEvent(event: TransactionHistoryEvent) {
        when (event) {
            is TransactionHistoryEvent.OnChangeStartCalendar -> _startCalendar.value = event.startCalendar
            is TransactionHistoryEvent.OnChangeEndCalendar -> _endCalendar.value = event.endCalendar

            is TransactionHistoryEvent.OnChangePeriodFilter -> _selectedPeriod.value = event.selectedPeriodFilter

            is TransactionHistoryEvent.PickersInput -> {
                when (event.selectPicker) {
                    Pickers.DATE_RANGE_PICKER -> _dateRangePicker.value = event.setInto
                    Pickers.START_TIME_PICKER -> _startTimePicker.value = event.setInto
                    Pickers.END_TIME_PICKER -> _endTimePicker.value = event.setInto
                }
            }

            is TransactionHistoryEvent.OnClickColumnsDropDown -> _showColumnMenu.value = event.setInto
            is TransactionHistoryEvent.OnPickColumnsDropDown -> _selectedColumn.value = event.columnName

            is TransactionHistoryEvent.OnClickSortersDropDown -> _showSortMenu.value = event.setInto
            is TransactionHistoryEvent.OnPickSortersDropDown -> _selectedSort.value = event.sortDirection

            TransactionHistoryEvent.OnClickShowReportBtn -> {
                if (_startCalendar.value == null || _endCalendar.value == null) {
                    return
                }

                if (_isRequesting.value) {
                    return
                }
                _isRequesting.value = true

                orderItemUseCase.searchTransactions(
                    storeId = _storeId.intValue,
                    tenantId = _tenantId.intValue,
                    page = 1,
                    limit = 10,
                    dateFilter = DateFilter(
                        "created_at",
                        (_startCalendar.value!!.timeInMillis / 1000).toInt(),
                        (_endCalendar.value!!.timeInMillis / 1000).toInt()
                    ),
                    queryFilter = QueryFilter("created_at", true)
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

            is TransactionHistoryEvent.OnPageChange -> {
                val currentDto = _searchTransactionsDto.value
                if (currentDto == null) return

                // Count how many page will be
                val totalPages = ceil(((currentDto.totalCount / currentDto.limit).toDouble())).toInt()
                if (event.page < 0 || event.page > totalPages) return

                val limit = _itemsPerPage.value.value
                val page = event.page
                orderItemUseCase.searchTransactions(
                    storeId = _storeId.intValue,
                    tenantId = _tenantId.intValue,
                    page = page,
                    limit = limit,
                    dateFilter = DateFilter(
                        "created_at",
                        (_startCalendar.value!!.timeInMillis / 1000).toInt(),
                        (_endCalendar.value!!.timeInMillis / 1000).toInt()
                    ),
                    queryFilter = QueryFilter("created_at", true)
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

            is TransactionHistoryEvent.OnClickItemsPerPage -> {
                _itemsPerPage.value = event.value
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
                }

                tenantResource is Resource.Error || storeResource is Resource.Error -> {
                    // Navigate to login
                }

                tenantResource is Resource.Loading || storeResource is Resource.Loading -> {}
            }
        }.launchIn(viewModelScope)
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

enum class SortDirection(val label: String) {
    ASC("Latest First"),
    DESC("Oldest First")
}

enum class ColumnName(val label: String) {
    CREATED_AT("Created At"),
    AMOUNT("Amount")
}

enum class ItemsPerPage(val value: Int) {
    TEN(10),
    TWENTY(20),
    THIRTY(30)
}