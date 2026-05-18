package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponse(
    @SerializedName("created_order_item_id")
    val createdOrderItemId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("total_amount")
    val totalAmount: Int,
    @SerializedName("purchased_price")
    val purchasedPrice: Int,
)