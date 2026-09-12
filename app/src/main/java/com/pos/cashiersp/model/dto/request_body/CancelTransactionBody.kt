package com.pos.cashiersp.model.dto.request_body

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class CancelTransactionBody(
    @SerializedName("order_item_id")
    val orderItemId: Int,
    @SerializedName("transaction_id")
    val transactionId: String,
)