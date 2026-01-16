package com.pos.cashiersp.model.domain

// A special class that contains information when load cashier screen
data class CartItem(
    val category: Category,
    val storeStock: StoreStock,
    val item: Item,
    val quantity: Int = 0,
) {
    val id = item.itemId
}