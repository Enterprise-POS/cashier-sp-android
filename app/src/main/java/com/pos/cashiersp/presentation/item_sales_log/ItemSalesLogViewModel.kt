package com.pos.cashiersp.presentation.item_sales_log

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.domain.PurchasedItem
import com.pos.cashiersp.model.dto.CashierItem
import com.pos.cashiersp.model.dto.DateFilter
import com.pos.cashiersp.model.dto.response_body.PurchasedItemListLogsResponse
import com.pos.cashiersp.model.dto.toDomain
import com.pos.cashiersp.model.room_entity.toCashierItem
import com.pos.cashiersp.presentation.cashier.component.GeneralAlertDialogStatus
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnApplyFilter
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnChangeDraftColumn
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnChangePage
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnChangeSearchItemId
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnClearDateRange
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnDismissFilterBottomSheet
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnSetDraftAscending
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnSetDraftEndDate
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnSetDraftQuickRange
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnSetDraftStartDate
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnSetFilterSheetState
import com.pos.cashiersp.presentation.item_sales_log.ItemSalesLogEvent.OnSetScopeSelector
import com.pos.cashiersp.presentation.util.CalendarChipUtils
import com.pos.cashiersp.presentation.util.Filter
import com.pos.cashiersp.presentation.util.PurchasedItemListLogsRequestBody
import com.pos.cashiersp.presentation.util.SortColumn
import com.pos.cashiersp.presentation.util.StateStatus
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.DatabaseCacheMetadataUseCase
import com.pos.cashiersp.use_case.StoreStockUseCase
import com.pos.cashiersp.use_case.purchased_item_list_use_case.PurchasedItemListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class ItemSalesLogViewModel @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase,
    private val storeStockUseCase: StoreStockUseCase,
    private val purchasedItemListUseCase: PurchasedItemListUseCase,
    private val databaseCacheMetadataUseCase: DatabaseCacheMetadataUseCase,
) : ViewModel() {
    private val _tenantId = mutableIntStateOf(0)
    private val _storeId = mutableIntStateOf(0)
    private val _storeName = mutableStateOf("")
    private val _viewModelState = mutableStateOf(StateStatus())
    val viewModelState: State<StateStatus> = _viewModelState
    private val _informationDialogStatus = mutableStateOf(GeneralAlertDialogStatus())
    val informationDialogStatus: State<GeneralAlertDialogStatus> = _informationDialogStatus

    // * OrderSearchAndSortBar
    private val _sortColumn = mutableStateOf(SortColumn.DATE)
    val sortColumn: State<SortColumn> = _sortColumn
    private val _sortAscending = mutableStateOf(false)
    val sortAscending: State<Boolean> = _sortAscending
    private val _dateFilterStart = mutableStateOf<Long?>(null)
    val dateFilterStart: State<Long?> = _dateFilterStart
    private val _dateFilterEnd = mutableStateOf<Long?>(null)
    val dateFilterEnd: State<Long?> = _dateFilterEnd
    private val _selectedScope = mutableStateOf<SalesLogScope?>(null)
    val selectedScope: State<SalesLogScope?> = _selectedScope

    private val _showFilterSheet = mutableStateOf(false)
    val showFilterSheet: State<Boolean> = _showFilterSheet

    // OrderSearchAndSortBar
    private val _searchSortBarInp = mutableStateOf("")
    val searchSortBarInp: State<String> = _searchSortBarInp

    // Input for user, will only apply at search item scope
    private val _resolvedItemId = mutableStateOf<Int?>(null)

    // FilterBottomSheet (A value here take initial value state from another state)
    private val _draftColumn = mutableStateOf(_sortColumn.value)
    private val _draftAscending = mutableStateOf(_sortAscending.value)
    private val _draftStartDate = mutableStateOf(_dateFilterStart.value)
    private val _draftEndDate = mutableStateOf(_dateFilterEnd.value)

    // Private quick range so when user actually hit "cancel" the previous state will available
    private val _quickRange =
        mutableStateOf(if (_draftStartDate.value != null || _draftEndDate.value != null) QuickRange.CUSTOM else null)
    private val _draftQuickRange = mutableStateOf(_quickRange.value)
    val draftColumn: State<SortColumn> = _draftColumn
    val draftAscending: State<Boolean> = _draftAscending
    val draftStartDate: State<Long?> = _draftStartDate
    val draftEndDate: State<Long?> = _draftEndDate
    val draftQuickRange: State<QuickRange?> = _draftQuickRange

    // Cashier items
    private val _cashierItems = mutableStateOf<List<CashierItem>>(emptyList())
    val cashierItems: State<List<CashierItem>> = _cashierItems

    // All items
    private val _allItemsLog = mutableStateOf<List<PurchasedItem>>(listOf())
    val allItemLogs: State<List<PurchasedItem>> = _allItemsLog

    private val _totalAllItemLogs = mutableIntStateOf(0)

    //val totalAllItemLogs: State<Int> = _totalAllItemLogs
    private val _totalAllItemPages = mutableIntStateOf(-1) // -1 Will maintain if the scope ever opened or not
    val totalAllItemPages: State<Int> = _totalAllItemPages

    // Search items
    private val _searchItemLog = mutableStateOf<List<PurchasedItem>>(listOf())
    val searchItemLogs: State<List<PurchasedItem>> = _searchItemLog
    private val _totalSearchItemLogs = mutableIntStateOf(0)

    //val totalSearchItemLogs: State<Int> = _totalSearchItemLogs
    private val _totalSearchItemPages = mutableIntStateOf(0)
    val totalSearchItemPages: State<Int> = _totalSearchItemPages

    // Pagination
    private val _currentAllItemPage = mutableIntStateOf(1)
    val currentAllItemPage: State<Int> = _currentAllItemPage
    private val _currentSearchItemPage = mutableIntStateOf(1)
    val currentSearchItemPage: State<Int> = _currentSearchItemPage
    private val _limit = mutableIntStateOf(30)

    // UI Event
    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadStoreAndTenantData()
    }

    fun onEvent(event: ItemSalesLogEvent) {
        when (event) {
            is OnApplyFilter -> {
                if (_viewModelState.value.isLoading) {
                    _showFilterSheet.value = false
                    showError("Filter error", "Please wait. Another request still ongoing.")
                    return
                }
                if (_selectedScope.value == SalesLogScope.SINGLE_ITEM && _searchSortBarInp.value.isBlank()) {
                    _showFilterSheet.value = false
                    showError("Filter error", "Please fill the input before applying filter")
                    return
                }

                val scope = _selectedScope.value ?: return

                // Resolve item id up front (single-item scope only) so we can
                // bail out cleanly before mutating any applied-filter state.
                var itemId: Int? = null
                if (scope == SalesLogScope.SINGLE_ITEM) {
                    itemId = resolveItemId(_searchSortBarInp.value)
                    if (itemId == null) return // resolveItemId already showed the error
                    _resolvedItemId.value = itemId
                }

                _viewModelState.value = StateStatus(isLoading = true)

                _sortColumn.value = event.column
                _sortAscending.value = event.ascending
                _dateFilterStart.value = event.start
                _dateFilterEnd.value = event.end
                _quickRange.value = event.quickRange

                _showFilterSheet.value = false

                val dateFilter = buildDateFilter(event.start, event.end)
                val body = buildRequestBody(
                    scope = scope,
                    itemId = itemId,
                    page = 1,
                    sortColumn = event.column,
                    ascending = event.ascending,
                    dateFilter = dateFilter
                )

                fetchPurchasedItemLogs(
                    body = body,
                    scope = scope,
                    resetPage = true,
                    errorTitle = "Filter error"
                )
            }

            is OnSetFilterSheetState -> _showFilterSheet.value = event.show
            is OnSetScopeSelector -> {
                // This is quick show item when user visit the page to see all logs
                if (event.scope == SalesLogScope.ALL_ITEMS && (_selectedScope.value == null || _totalAllItemPages.intValue == -1)) {
                    _totalAllItemPages.intValue = 0 // Indicate user ever open the page

                    val dateFilter = buildDateFilter(_dateFilterStart.value, _dateFilterEnd.value)
                    val body = buildRequestBody(
                        scope = SalesLogScope.ALL_ITEMS,
                        itemId = null,
                        page = 1,
                        sortColumn = _sortColumn.value,
                        ascending = _sortAscending.value,
                        dateFilter = dateFilter
                    )

                    fetchPurchasedItemLogs(
                        body = body,
                        scope = SalesLogScope.ALL_ITEMS,
                        resetPage = true,
                        errorTitle = "Failed to load items"
                    )
                }

                _selectedScope.value = event.scope
            }

            is OnChangeSearchItemId -> {
                _searchSortBarInp.value = event.inputId
                // Editing the search text invalidates the last resolved id —
                // user must re-apply the filter before paging with new text.
                _resolvedItemId.value = null
            }

            is OnChangeDraftColumn -> _draftColumn.value = event.sortColumn
            is OnSetDraftAscending -> _draftAscending.value = event.setTo
            is OnSetDraftStartDate -> _draftStartDate.value = event.setStartDate
            is OnSetDraftEndDate -> _draftEndDate.value = event.setEndDate
            is OnSetDraftQuickRange -> _draftQuickRange.value = event.setQuickRange
            OnClearDateRange -> {
                _draftStartDate.value = null
                _draftEndDate.value = null
                _draftQuickRange.value = null
            }

            OnDismissFilterBottomSheet -> {
                // Will reset the draftState into set state before
                _draftColumn.value = _sortColumn.value
                _draftAscending.value = _sortAscending.value
                _draftStartDate.value = _dateFilterStart.value
                _draftEndDate.value = _dateFilterEnd.value
                _draftQuickRange.value = _quickRange.value
                this.onEvent(OnSetFilterSheetState(false))
            }

            is OnChangePage -> {
                val goToPage = event.goToPage
                if (goToPage <= 0) return

                if (_viewModelState.value.isLoading) {
                    showError("Page error", "Please wait. Another request still ongoing.")
                    return
                }

                val scope = _selectedScope.value ?: return

                var itemId: Int? = null
                if (scope == SalesLogScope.SINGLE_ITEM) {
                    itemId = _resolvedItemId.value
                    if (itemId == null) {
                        showError("Page error", "Please apply a filter with a valid item before changing page")
                        return
                    }
                    if (goToPage > _totalSearchItemPages.intValue) return
                    _currentSearchItemPage.intValue = goToPage
                } else {
                    if (goToPage > _totalAllItemPages.intValue) return
                    _currentAllItemPage.intValue = goToPage
                }

                val dateFilter = buildDateFilter(_dateFilterStart.value, _dateFilterEnd.value)
                val body = buildRequestBody(
                    scope = scope,
                    itemId = itemId,
                    page = goToPage,
                    sortColumn = _sortColumn.value,
                    ascending = _sortAscending.value,
                    dateFilter = dateFilter
                )

                fetchPurchasedItemLogs(
                    body = body,
                    scope = scope,
                    resetPage = false,
                    errorTitle = "Failed to change page"
                )
            }

            ItemSalesLogEvent.OnDismissInformationDialog -> {
                _informationDialogStatus.value = GeneralAlertDialogStatus()
            }

            is ItemSalesLogEvent.OnClickSeeOrderItemDetail -> {
                val id = event.orderItemId
                viewModelScope.launch {
                    _uiEvent.emit(UIEvent.GotoInvoiceDetailScreen(id))
                }
            }
        }
    }

    /**
     * Resolves a numeric item id, either directly (if [rawInput] is digits-only)
     * or by matching [rawInput] against a cached item's name (case-insensitive
     * substring match). Shows an error dialog and returns null on failure.
     */
    private fun resolveItemId(rawInput: String): Int? {
        if (rawInput.isDigitsOnly()) {
            return rawInput.toInt()
        }
        val match = _cashierItems.value.firstOrNull { item ->
            item.itemName.contains(rawInput, ignoreCase = true)
        }
        if (match == null) {
            showError(
                "Filter error",
                "Not available item for searching. Please try again from suggestion or type item id"
            )
            return null
        }
        return match.itemId
    }

    // DateFilter column property is empty string because column property is deprecated
    private fun buildDateFilter(start: Long?, end: Long?): DateFilter {
        val startDate = start?.toInt() ?: 0
        val endDate = end?.toInt() ?: CalendarChipUtils.nowEpoch().toInt()
        return DateFilter(column = "", startDate = startDate, endDate = endDate)
    }

    private fun buildRequestBody(
        scope: SalesLogScope,
        itemId: Int?,
        page: Int,
        sortColumn: SortColumn,
        ascending: Boolean,
        dateFilter: DateFilter
    ): PurchasedItemListLogsRequestBody {
        val itemIds = if (scope == SalesLogScope.SINGLE_ITEM && itemId != null) listOf(itemId) else listOf()
        return PurchasedItemListLogsRequestBody(
            itemIds = itemIds,
            storeId = _storeId.intValue,
            limit = _limit.intValue,
            page = page,
            filters = listOf(
                // sort by, asc / desc
                Filter(column = sortColumn, ascending = ascending),
            ),
            dateFilter = dateFilter
        )
    }

    /**
     * Shared fetch + state-update logic used by OnApplyFilter, the initial
     * ALL_ITEMS load in OnSetScopeSelector, and OnChangePage — keeps loading/
     * error/success handling identical across all three call sites.
     */
    private fun fetchPurchasedItemLogs(
        body: PurchasedItemListLogsRequestBody,
        scope: SalesLogScope,
        resetPage: Boolean,
        errorTitle: String
    ) {
        purchasedItemListUseCase.purchasedItemListLogs(body, _tenantId.intValue).onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    println(resource.message)
                    showError(errorTitle, resource.message ?: "Unknown error occurred")
                }

                is Resource.Loading -> {
                    _viewModelState.value = StateStatus(isLoading = true)
                }

                is Resource.Success -> {
                    val data: PurchasedItemListLogsResponse = resource.data!!
                    val logs = data.logs.map { it.toDomain() }
                    val pages = ceil(data.totalCount.toDouble() / _limit.intValue).toInt()

                    if (scope == SalesLogScope.ALL_ITEMS) {
                        _allItemsLog.value = logs
                        _totalAllItemLogs.intValue = data.totalCount
                        _totalAllItemPages.intValue = pages
                        if (resetPage) _currentAllItemPage.intValue = 1
                    } else {
                        _searchItemLog.value = logs
                        _totalSearchItemLogs.intValue = data.totalCount
                        _totalSearchItemPages.intValue = pages
                        if (resetPage) _currentSearchItemPage.intValue = 1
                    }
                    _viewModelState.value = StateStatus()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun showError(title: String, message: String) {
        _viewModelState.value = StateStatus() // clear loading; error no longer travels via this state
        _informationDialogStatus.value = GeneralAlertDialogStatus.error(title, message)
    }

    private fun loadStoreAndTenantData() {
        combine(
            dataStoreUseCase.getCurrentTenant(),
            dataStoreUseCase.getCurrentStore()
        ) { tenant, store -> Pair(tenant, store) }
            .onEach { (tenantResource, storeResource) ->
                when {
                    tenantResource is Resource.Success && storeResource is Resource.Success -> {
                        val tenantId = tenantResource.data!!.id
                        val storeId = storeResource.data!!.id
                        _tenantId.intValue = tenantId
                        _storeId.intValue = storeId
                        _storeName.value = storeResource.data.name

                        // Check for cache
                        val cachedCashierItem = getCache(tenantId, storeId, onError = { message ->
                            showError("Failed to load cache", message)
                        })

                        if (cachedCashierItem.isNotEmpty()) {
                            _cashierItems.value = cachedCashierItem
                        }

                        _viewModelState.value = StateStatus()
                    }

                    tenantResource is Resource.Error || storeResource is Resource.Error -> {
                        println("Error happened at load tenant and store resource")
                        _informationDialogStatus.value = GeneralAlertDialogStatus.error(
                            "Application error",
                            "Could not load tenant information. This may happen because user not logged in. Try logout and sign in again.\nDetail: ${tenantResource.message}"
                        )
                    }

                    else -> { /* Loading — do nothing */
                        _viewModelState.value = StateStatus(isLoading = true)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun getCache(tenantId: Int, storeId: Int, onError: (message: String) -> Unit): List<CashierItem> {
        val metadataResource = databaseCacheMetadataUseCase.getMetadata(storeId, tenantId).lastOrNull()
        if (metadataResource?.data == null) return emptyList()

        val cacheResult = storeStockUseCase
            .getCachedCashierItems(_tenantId.intValue, _storeId.intValue)
            .filter { it !is Resource.Loading }
            .lastOrNull()

        return when (cacheResult) {
            is Resource.Success -> cacheResult.data?.map { it.toCashierItem() } ?: emptyList()
            is Resource.Error -> {
                // On error show something
                onError(cacheResult.message ?: "[Unknown error] while get cache data. Please contact developer")
                emptyList()
            }

            else -> emptyList()
        }
    }

    // UI Events
    sealed class UIEvent {
        data class ErrorAndMustNavigateToSelectTenantScreen(val message: String) : UIEvent()
        data class GotoInvoiceDetailScreen(val orderItemId: Int) : UIEvent()
    }
}