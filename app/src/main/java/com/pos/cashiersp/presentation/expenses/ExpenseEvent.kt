package com.pos.cashiersp.presentation.expenses

import com.pos.cashiersp.model.domain.Expense
import java.util.Calendar

sealed class ExpenseEvent {
    // List + filters
    data class OnSetScope(val scope: ExpenseScope) : ExpenseEvent()
    data class OnSetCategoryFilter(val categoryId: Int) : ExpenseEvent() // 0 = all, -1 = uncategorized
    data class OnSetDateRange(val startDayMillis: Long, val endDayMillis: Long) : ExpenseEvent() // local start of day
    data object OnLoadMore : ExpenseEvent()
    data object OnRefresh : ExpenseEvent()
    data object OnDismissListError : ExpenseEvent()
    data object OnRetrySetup : ExpenseEvent() // reload categories + store names

    // Add / edit form
    data object OnOpenAddForm : ExpenseEvent()
    data class OnOpenEditForm(val expense: Expense) : ExpenseEvent()
    data object OnCloseForm : ExpenseEvent()
    data class OnChangeAmount(val digits: String) : ExpenseEvent()
    data class OnChangeCategory(val categoryId: Int?) : ExpenseEvent()
    data class OnChangeForStore(val forStore: Boolean) : ExpenseEvent()
    data class OnChangePaymentType(val paymentType: String) : ExpenseEvent()
    data class OnChangeDate(val date: Calendar) : ExpenseEvent()
    data class OnChangeDescription(val description: String) : ExpenseEvent()
    data object OnSaveForm : ExpenseEvent()
    data object OnDeleteEditing : ExpenseEvent()

    // Category manager
    data class OnSetCategoryManager(val open: Boolean) : ExpenseEvent()
    data class OnChangeNewCategoryName(val name: String) : ExpenseEvent()
    data object OnAddCategory : ExpenseEvent()
    data class OnStartRenameCategory(val categoryId: Int?, val name: String = "") : ExpenseEvent() // null = cancel
    data class OnChangeRenameCategory(val name: String) : ExpenseEvent()
    data object OnSaveRenameCategory : ExpenseEvent()
    data class OnDeleteCategory(val categoryId: Int) : ExpenseEvent()
}
