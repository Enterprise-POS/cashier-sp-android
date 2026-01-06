package com.pos.cashiersp.model.domain

import com.pos.cashiersp.model.dto.StockType

data class Item(
    val itemId: Int,
    val tenantId: Int,
    val itemName: String,
    val stocks: Int,
    val isActive: Boolean,
    val stockType: StockType,
)
