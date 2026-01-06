package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTransactionParams(
    @SerializedName("items")
    val items: List<Item>,

    @SerializedName("purchased_price")
    val purchasedPrice: Int,
    @SerializedName("total_quantity")
    val totalQuantity: Int,
    @SerializedName("total_amount")
    val totalAmount: Int,
    @SerializedName("discount_amount")
    val discountAmount: Int,
    @SerializedName("sub_total")
    val subTotal: Int,

    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("tenant_id")
    val tenantId: Int,
    @SerializedName("store_id")
    val storeId: Int,
)