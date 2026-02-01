package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SearchTransactionsDto(
    val limit: Int,
    @SerializedName("order_items")
    val orderItems: List<OrderItem>,
    val page: Int,
    @SerializedName("requested_by")
    val requestedBy: Int,
    @SerializedName("requested_by_tenant_id")
    val requestedByTenantId: Int,
    @SerializedName("total_count")
    val totalCount: Int
)