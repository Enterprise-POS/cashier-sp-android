package com.pos.cashiersp.model.room_entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pos.cashiersp.model.dto.StockType

@Entity
data class CashierItemEntity(
    val categoryId: Int,
    val categoryName: String,
    val isActive: Boolean,
    val itemId: Int,
    val itemName: String,
    val stockType: StockType,
    val stocks: Int,
    @PrimaryKey val storeStockId: Int,
    val storeStockPrice: Int,
    val storeStockStocks: Int,

    // This will allow separation with another store and tenant
    val storeId: Int,
    val tenantId: Int,
)