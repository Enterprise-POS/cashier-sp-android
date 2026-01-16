package com.pos.cashiersp.model.dto


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class GetTenantWithUserDto(
    val tenants: List<Tenant>,
    @SerializedName("requested_by")
    val requestedBy: Int,
)