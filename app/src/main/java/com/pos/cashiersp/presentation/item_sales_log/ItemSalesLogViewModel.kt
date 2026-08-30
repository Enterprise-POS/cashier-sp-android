package com.pos.cashiersp.presentation.item_sales_log

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ItemSalesLogViewModel @Inject constructor() : ViewModel() {
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

    fun onEvent(event: ItemSalesLogEvent) {
        when (event) {
            is ItemSalesLogEvent.OnApplyFilter -> {
                _sortColumn.value = event.column
                _sortAscending.value = event.ascending
                _dateFilterStart.value = event.start
                _dateFilterEnd.value = event.end
                _quickRange.value = event.quickRange

                _showFilterSheet.value = false
            }

            is ItemSalesLogEvent.OnSetFilterSheetState -> _showFilterSheet.value = event.show
            is ItemSalesLogEvent.OnSetScopeSelector -> _selectedScope.value = event.scope

            is ItemSalesLogEvent.OnChangeSearchItemId -> _searchSortBarInp.value = event.inp

            is ItemSalesLogEvent.OnChangeDraftColumn -> _draftColumn.value = event.sortColumn
            is ItemSalesLogEvent.OnSetDraftAscending -> _draftAscending.value = event.setTo
            is ItemSalesLogEvent.OnSetDraftStartDate -> _draftStartDate.value = event.setStartDate
            is ItemSalesLogEvent.OnSetDraftEndDate -> _draftEndDate.value = event.setEndDate
            is ItemSalesLogEvent.OnSetDraftQuickRange -> _draftQuickRange.value = event.setQuickRange
            ItemSalesLogEvent.OnClearDateRange -> {
                _draftStartDate.value = null
                _draftEndDate.value = null
                _draftQuickRange.value = null
            }

            ItemSalesLogEvent.OnDismissFilterBottomSheet -> {
                // Will reset the draftState into set state before
                _draftColumn.value = _sortColumn.value
                _draftAscending.value = _sortAscending.value
                _draftStartDate.value = _dateFilterStart.value
                _draftEndDate.value = _dateFilterEnd.value
                _draftQuickRange.value = _quickRange.value
                this.onEvent(ItemSalesLogEvent.OnSetFilterSheetState(falseBo))
            }
        }
    }
}