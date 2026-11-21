package com.pos.cashiersp.model.dto.request_body

import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.model.dto.DateFilter
import com.pos.cashiersp.model.dto.QueryFilter

// Create + edit. PUT replaces every field, so always send the whole form.
// Gson skips nulls: a null store_id / expense_category_id is sent as "not set" = whole business / uncategorized
data class ExpenseRequestBody(
    @SerializedName("expense_id")
    val expenseId: Int? = null, // edit only
    @SerializedName("store_id")
    val storeId: Int?,
    @SerializedName("expense_category_id")
    val expenseCategoryId: Int?,
    val amount: Long,
    val description: String,
    @SerializedName("expense_date")
    val expenseDate: Long, // epoch seconds
    @SerializedName("payment_type")
    val paymentType: String,
)

data class SearchExpensesRequestBody(
    @SerializedName("store_id")
    val storeId: Int, // 0 = all, -1 = whole business only
    @SerializedName("expense_category_id")
    val expenseCategoryId: Int, // 0 = all, -1 = uncategorized
    val limit: Int,
    val page: Int,
    val filters: List<QueryFilter>,
    @SerializedName("date_filter")
    val dateFilter: DateFilter?,
)

data class ExpenseIdBody(@SerializedName("expense_id") val expenseId: Int)

data class ExpenseCategoryRequestBody(
    @SerializedName("expense_category_id")
    val expenseCategoryId: Int? = null,
    val name: String? = null,
)
