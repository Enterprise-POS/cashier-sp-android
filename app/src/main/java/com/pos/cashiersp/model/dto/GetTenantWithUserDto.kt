package com.pos.cashiersp.model.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetTenantWithUserDto(
    val tenants: List<Tenant>,
    @SerialName("requested_by")
    val requestedBy: Int,
)