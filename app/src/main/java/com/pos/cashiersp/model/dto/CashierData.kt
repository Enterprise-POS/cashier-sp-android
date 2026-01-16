package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.model.room_entity.CashierItemEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CashierItem(
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("item_name")
    val itemName: String,
    @SerializedName("stock_type")
    val stockType: StockType,
    val stocks: Int,
    @SerializedName("store_stock_id")
    val storeStockId: Int,
    @SerializedName("store_stock_price")
    val storeStockPrice: Int,
    @SerializedName("store_stock_stocks")
    val storeStockStocks: Int
)

fun CashierItem.toDBEntity(tenantId: Int, storeId: Int): CashierItemEntity {
    return CashierItemEntity(
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        isActive = this.isActive,
        itemId = this.itemId,
        itemName = this.itemName,
        stockType = this.stockType,
        stocks = this.stocks,
        storeStockId = this.storeStockId,
        storeStockPrice = this.storeStockPrice,
        storeStockStocks = this.storeStockStocks,
        tenantId = tenantId,
        storeId = storeId,
    )
}
