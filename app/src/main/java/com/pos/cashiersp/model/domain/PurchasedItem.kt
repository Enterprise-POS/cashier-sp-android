package com.pos.cashiersp.model.domain

data class PurchasedItem(
    val discountAmount: Int,
    val id: Int,
    val itemId: Int,
    val itemNameSnapshot: String,
    val orderItemId: Int,

    val storePriceSnapshot: Int,
    val quantity: Int,
    val totalAmount: Int
)
