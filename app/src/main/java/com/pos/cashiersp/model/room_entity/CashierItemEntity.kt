package com.pos.cashiersp.model.room_entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pos.cashiersp.model.dto.CashierItem
import com.pos.cashiersp.model.dto.StockType

@Entity
data class CashierItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val categoryName: String,
    val isActive: Boolean,
    val itemId: Int,
    val itemName: String,
    val stockType: StockType,
    val stocks: Int,
    val storeStockId: Int,
    val storeStockPrice: Int,
    val storeStockStocks: Int,
    val basePrice: Int,

    // This will allow separation with another store and tenant
    val storeId: Int,
    val tenantId: Int,
)

fun CashierItemEntity.toCashierItem(): CashierItem {
    return CashierItem(
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        isActive = this.isActive,
        itemId = this.itemId,
        itemName = this.itemName,
        stockType = this.stockType,
        stocks = this.stocks,
        basePrice = this.basePrice,
        storeStockId = this.storeStockId,
        storeStockPrice = this.storeStockPrice,
        storeStockStocks = this.storeStockStocks,
    )
}
