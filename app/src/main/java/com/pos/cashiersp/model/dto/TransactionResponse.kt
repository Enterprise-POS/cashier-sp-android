package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponse(
    @SerializedName("transaction_id")
    val transactionId: Int
)