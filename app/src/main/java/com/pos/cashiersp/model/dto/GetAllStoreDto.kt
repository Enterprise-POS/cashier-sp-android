package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAllStoreDto(
    val count: Int,
    val limit: Int,
    val page: Int,
    val stores: List<Store>,

    @SerializedName("requested_by_tenant_id")
    val requestedByTenantId: Int,
)