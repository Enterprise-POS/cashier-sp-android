package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.model.domain.StoreStock
import kotlinx.serialization.Serializable

/*
* A combination from store_stock with warehouse
* */

enum class StockType {
    UNLIMITED,
    TRACKED
}

@Serializable
data class StoreStockV2(
    @SerializedName("created_at")
    val createdAt: String,
    val id: Int,
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("item_name")
    val itemName: String,
    val price: Int,
    val stocks: Int,
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("stock_type")
    val stockType: StockType,
    @SerializedName("base_price")
    val basePrice: Int,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("category_name")
    val categoryName: String,
)

fun StoreStockV2.toDomain(): StoreStock {
    return StoreStock(
        id = this.id,
        itemId = this.itemId,
        itemName = this.itemName,
        price = this.price,
        stocks = this.stocks,
        createdAt = this.createdAt,
        stockType = this.stockType,

        // Will ignore for now (2026-2-21)
        lastUpdate = "",
        storeId = 0,
    )
}
