package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.presentation.util.PaymentMethod
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
    @SerializedName("payment_type")
    val paymentType: PaymentMethod,
    @SerializedName("payment_token")
    val paymentToken: String,
    @SerializedName("payment_url")
    val paymentURL: String
)