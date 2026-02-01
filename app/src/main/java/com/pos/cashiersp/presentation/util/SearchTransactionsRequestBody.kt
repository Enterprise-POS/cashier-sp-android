package com.pos.cashiersp.presentation.util


import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.model.dto.DateFilter
import com.pos.cashiersp.model.dto.QueryFilter
import kotlinx.serialization.Serializable

@Serializable
data class SearchTransactionsRequestBody(
    @SerializedName("date_filter")
    val dateFilter: DateFilter,
    @SerializedName("filters")
    val filters: List<QueryFilter>,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("store_id")
    val storeId: Int,
    @SerializedName("tenant_id")
    val tenantId: Int
)