package com.pos.cashiersp.presentation.transaction_history

import java.util.Calendar

sealed class TransactionHistoryEvent {
    data class OnChangeStartCalendar(val startCalendar: Calendar?) : TransactionHistoryEvent()
    data class OnChangeEndCalendar(val endCalendar: Calendar?) : TransactionHistoryEvent()

    data class OnChangePeriodFilter(val selectedPeriodFilter: PeriodFilter) : TransactionHistoryEvent()
    data class PickersInput(val selectPicker: Pickers, val setInto: Boolean) : TransactionHistoryEvent()

    data class OnPickSortersDropDown(val sortDirection: SortDirection) : TransactionHistoryEvent()
    data class OnClickSortersDropDown(val setInto: Boolean) : TransactionHistoryEvent()

    data class OnPickColumnsDropDown(val columnName: ColumnName) : TransactionHistoryEvent()
    data class OnClickColumnsDropDown(val setInto: Boolean) : TransactionHistoryEvent()

    data class OnClickItemsPerPage(val value: ItemsPerPage) : TransactionHistoryEvent()

    data class OnPageChange(val page: Int) : TransactionHistoryEvent()

    object OnClickShowReportBtn : TransactionHistoryEvent()
}