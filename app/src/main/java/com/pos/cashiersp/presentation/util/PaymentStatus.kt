package com.pos.cashiersp.presentation.util

enum class PaymentStatus(val uiLabel: String) {
    SUCCESS("Success"),
    PENDING("Pending"),
    REFUNDED("Refunded"),
    FAILED("Failed"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled"),
    PARTIALY_REFUNDED("Partialy Refunded")
}