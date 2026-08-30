package com.pos.cashiersp.presentation.item_sales_log

sealed class ItemSalesLogEvent {
    data class OnApplyFilter(val column: SortColumn, val ascending: Boolean, val start: Long?, val end: Long?) :
        ItemSalesLogEvent()

    data class OnSetFilterSheetState(val show: Boolean) : ItemSalesLogEvent()
    data class OnSetScopeSelector(val scope: SalesLogScope) : ItemSalesLogEvent()

    data class OnChangeSearchItemId(val inp: String) : ItemSalesLogEvent()

    // FilterBottomSheet
    data class OnChangeDraftColumn(val sortColumn: SortColumn) : ItemSalesLogEvent()
}