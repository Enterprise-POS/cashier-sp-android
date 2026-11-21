package com.pos.cashiersp.model.dto

import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.model.domain.Expense
import com.pos.cashiersp.model.domain.ExpenseCategory
import com.pos.cashiersp.presentation.util.parseDateString

data class ExpenseDto(
    val id: Int,
    @SerializedName("store_id")
    val storeId: Int?, // null = whole business
    @SerializedName("expense_category_id")
    val expenseCategoryId: Int?, // null = uncategorized
    @SerializedName("expense_category_name")
    val expenseCategoryName: String?,
    val amount: Long,
    val description: String,
    @SerializedName("expense_date")
    val expenseDate: String,
    @SerializedName("payment_type")
    val paymentType: String,
    @SerializedName("responsible_user_name_snapshot")
    val responsibleUserNameSnapshot: String,
    @SerializedName("updated_at")
    val updatedAt: String?,
)

fun ExpenseDto.toDomain() = Expense(
    id = id,
    storeId = storeId,
    categoryId = expenseCategoryId,
    categoryName = expenseCategoryName,
    amount = amount,
    description = description,
    expenseDate = parseDateString(expenseDate),
    paymentType = paymentType,
    recordedBy = responsibleUserNameSnapshot,
    updatedAt = updatedAt?.let { parseDateString(it) },
)

data class ExpenseCategoryDto(
    val id: Int,
    val name: String,
)

fun ExpenseCategoryDto.toDomain() = ExpenseCategory(id = id, name = name)

data class SearchExpensesDto(
    val expenses: List<ExpenseDto>,
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("sum_amount")
    val sumAmount: Long, // every matched row, not only this page
)

data class ExpenseCategoriesDto(
    @SerializedName("expense_categories")
    val expenseCategories: List<ExpenseCategoryDto>,
)

data class CreatedExpenseDto(@SerializedName("created_expense") val expense: ExpenseDto)
data class EditedExpenseDto(@SerializedName("edited_expense") val expense: ExpenseDto)
data class CreatedExpenseCategoryDto(@SerializedName("created_expense_category") val category: ExpenseCategoryDto)
data class EditedExpenseCategoryDto(@SerializedName("edited_expense_category") val category: ExpenseCategoryDto)
