package com.pos.cashiersp.model.domain

data class StoreStock(
    val id: Int,
    val itemId: Int,
    val itemName: String,
    val price: Int,
    val stocks: Int,
    val createdAt: String,
    val lastUpdate: String,
    val storeId: Int,
)
