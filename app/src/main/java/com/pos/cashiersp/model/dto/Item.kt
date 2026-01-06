package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// This class is used by CreateTransactionParams
@Serializable
data class Item(
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("purchased_price")
    val purchasedPrice: Int,
    @SerializedName("discount_amount")
    val discountAmount: Int,
    @SerializedName("total_amount")
    val totalAmount: Int
)