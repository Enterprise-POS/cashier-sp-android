package com.pos.cashiersp.model.dto.response_body

import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.model.dto.PurchasedItemDto
import kotlinx.serialization.Serializable

@Serializable
data class PurchasedItemListLogsResponse(
    @SerializedName("logs")
    val logs: List<PurchasedItemDto>,
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("requested_by_tenant_id")
    val requestedByTenantId: Int,
)
