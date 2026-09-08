package com.pos.cashiersp.model.domain

import com.pos.cashiersp.presentation.util.PaymentMethod
import com.pos.cashiersp.presentation.util.PaymentStatus
import java.util.Calendar


data class OrderItem(
    val createdAt: Calendar,
    val discountAmount: Int,
    val id: Int,
    val purchasedPrice: Int,
    val storeId: Int,
    val subtotal: Int,
    val tenantId: Int,
    val totalAmount: Int,
    val totalQuantity: Int,
    val paymentMethod: PaymentMethod,
    val transactionId: String,
    val paymentStatus: PaymentStatus,
    val paymentURL: String,
    val paymentToken: String,

    // Store
    val storeName: String = "",
    val address: String = "",
    val phoneNumber: String = "",
)