package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class StoreStockGetV2Dto(
    @SerializedName("count")
    val count: Int,
    @SerializedName("store_stocks")
    val storeStocks: List<StoreStockV2>
)