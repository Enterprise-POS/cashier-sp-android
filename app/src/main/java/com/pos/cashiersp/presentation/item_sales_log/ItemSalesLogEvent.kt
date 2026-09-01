package com.pos.cashiersp.presentation.item_sales_log

import com.pos.cashiersp.model.dto.CashierItem

sealed class ItemSalesLogEvent {
    data class OnApplyFilter(
        val column: SortColumn,
        val ascending: Boolean,
        val start: Long?,
        val end: Long?,
        val quickRange: QuickRange?
    ) :
        ItemSalesLogEvent()

    data class OnSetFilterSheetState(val show: Boolean) : ItemSalesLogEvent()
    data class OnSetScopeSelector(val scope: SalesLogScope) : ItemSalesLogEvent()

    data class OnChangeSearchItemId(val inputId: String) : ItemSalesLogEvent()

    // FilterBottomSheet
    data class OnChangeDraftColumn(val sortColumn: SortColumn) : ItemSalesLogEvent()
    data class OnSetDraftAscending(val setTo: Boolean) : ItemSalesLogEvent()
    data class OnSetDraftStartDate(val setStartDate: Long?) : ItemSalesLogEvent()
    data class OnSetDraftEndDate(val setEndDate: Long?) : ItemSalesLogEvent()
    data class OnSetDraftQuickRange(val setQuickRange: QuickRange?) : ItemSalesLogEvent()
    object OnClearDateRange : ItemSalesLogEvent()

    object OnDismissFilterBottomSheet : ItemSalesLogEvent()

    // Pagination
    data class OnChangePage(val goToPage: Int) : ItemSalesLogEvent()
}