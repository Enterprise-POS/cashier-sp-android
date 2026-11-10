package com.pos.cashiersp.model.domain

import com.pos.cashiersp.model.dto.VoucherType

data class Voucher(
    val id: Int,
    val code: String,
    val name: String,
    val tenantId: Int,
    val createdAt: String?,
    val voucherType: VoucherType,
    val usedCount: Int,
    val usageLimit: Int,
    val startAt: String?,
    val endAt: String?,
    val percentage: Int,
    val maximumDecrement: Int,
    val roundingPolicy: String,
    val decrement: Int,
    val itemIdCombinations: List<Int>,
    val itemQuantities: Int,
)
