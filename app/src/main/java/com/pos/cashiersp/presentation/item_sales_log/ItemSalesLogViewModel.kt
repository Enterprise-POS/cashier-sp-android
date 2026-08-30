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
    val draftColumn: State<SortColumn> = _draftColumn

    fun onEvent(event: ItemSalesLogEvent) {
        when (event) {
            is ItemSalesLogEvent.OnApplyFilter -> {
                _sortColumn.value = event.column
                _sortAscending.value = event.ascending
                _dateFilterStart.value = event.start
                _dateFilterEnd.value = event.end

                _showFilterSheet.value = false
            }

            is ItemSalesLogEvent.OnSetFilterSheetState -> _showFilterSheet.value = event.show
            is ItemSalesLogEvent.OnSetScopeSelector -> _selectedScope.value = event.scope

            is ItemSalesLogEvent.OnChangeSearchItemId -> _searchSortBarInp.value = event.inp

            is ItemSalesLogEvent.OnChangeDraftColumn -> _draftColumn.value = event.sortColumn
        }
    }
}