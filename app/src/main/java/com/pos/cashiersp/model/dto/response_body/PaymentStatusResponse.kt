package com.pos.cashiersp.model.dto.response_body

import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.presentation.util.PaymentStatus

data class PaymentStatusResponse(
    @SerializedName("payment_status")
    val paymentStatus: PaymentStatus,
    @SerializedName("status_code")
    val statusCode: String,
    @SerializedName("message")
    val message: String,
)
