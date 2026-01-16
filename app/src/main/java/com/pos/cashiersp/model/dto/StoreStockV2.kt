package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
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
    val stockType: StockType
)