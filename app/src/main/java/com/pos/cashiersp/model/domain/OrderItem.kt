package com.pos.cashiersp.model.domain

import com.pos.cashiersp.presentation.util.PaymentMethod
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

    // Store
    val storeName: String = "",
    val address: String = "",
    val phoneNumber: String = "",
)