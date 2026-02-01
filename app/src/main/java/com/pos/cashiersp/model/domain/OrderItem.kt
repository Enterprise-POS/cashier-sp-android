package com.pos.cashiersp.model.domain

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
    val totalQuantity: Int
)