package com.pos.cashiersp.model.dto

import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.model.domain.Voucher
import kotlinx.serialization.Serializable

@Serializable
enum class VoucherType {
    FROM_TOTAL_LIMITED_PERCENTAGE,
    FROM_TOTAL_LIMITED_STATIC_DECREMENT,
    ITEM_COMBINATIONS,
    ITEM_QUANTITIES,
}

@Serializable
data class VoucherDto(
    val id: Int,
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

fun VoucherDto.toDomain(): Voucher {
    return Voucher(
        id = id,
        code = code,
        name = name,
        tenantId = tenantId,
        createdAt = createdAt,
        voucherType = voucherType,
        usedCount = usedCount,
        usageLimit = usageLimit,
        startAt = startAt,
        endAt = endAt,
        percentage = percentage,
        maximumDecrement = maximumDecrement,
        roundingPolicy = roundingPolicy,
        decrement = decrement,
        itemIdCombinations = itemIdCombinations.toList(),
        itemQuantities = itemQuantities,
    )
}
