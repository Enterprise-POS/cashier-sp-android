package com.pos.cashiersp.model.domain

import java.util.Calendar

data class Expense(
    val id: Int,
    val storeId: Int?, // null = whole business
    val categoryId: Int?, // null = uncategorized
    val categoryName: String?,
    val amount: Long,
    val description: String,
    val expenseDate: Calendar,
    val paymentType: String,
    val recordedBy: String,
    val updatedAt: Calendar?,
)

data class ExpenseCategory(
    val id: Int,
    val name: String,
)

// API values for payment_type (same as order_item)
val ExpensePaymentTypes = listOf("CASH", "QRIS", "CARD", "EWALLET", "OTHER")
