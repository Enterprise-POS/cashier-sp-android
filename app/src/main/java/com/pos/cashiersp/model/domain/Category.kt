package com.pos.cashiersp.model.domain

import java.util.Date

data class Category(
    val id: Int,
    val categoryName: String,
    val tenantId: Int,
    val createdAt: Date,

    // This count is not related to any table
    // helper property to count how much current item in this category
    // example use case: CashierScreen
    var count: Int = 0
)
