package com.pos.cashiersp.model.dto

import com.google.gson.annotations.SerializedName

enum class VoucherType {
    FROM_TOTAL_LIMITED_PERCENTAGE,
    FROM_TOTAL_LIMITED_STATIC_DECREMENT,
    ITEM_COMBINATIONS,
    ITEM_QUANTITIES,
}

data class Voucher(
    val id: Int? = null,
    val code: String,
    val name: String,
    @SerializedName("tenant_id")
    val tenantId: Int,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("voucher_type")
    val voucherType: VoucherType,
    @SerializedName("used_count")
    val usedCount: Int,
    @SerializedName("usage_limit")
    val usageLimit: Int,
    @SerializedName("start_at")
    val startAt: String? = null,
    @SerializedName("end_at")
    val endAt: String? = null,
    val percentage: Int,
    @SerializedName("maximum_decrement")
    val maximumDecrement: Int,
    @SerializedName("rounding_policy")
    val roundingPolicy: String,
    val decrement: Int,
    @SerializedName("item_id_combinations")
    val itemIdCombinations: List<Int>,
    @SerializedName("item_quantities")
    val itemQuantities: Int,
)
