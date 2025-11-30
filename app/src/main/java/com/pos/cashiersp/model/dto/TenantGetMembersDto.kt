package com.pos.cashiersp.model.dto

import com.google.gson.annotations.SerializedName
import com.pos.cashiersp.model.dto.Member
import com.pos.cashiersp.model.domain.TenantGetMembers

data class TenantGetMembersDto(
    val members: List<Member>,
    @SerializedName("requested_by")
    val requestedBy: Int,
    @SerializedName("requested_tenant")
    val requestedTenant: Int
)
fun TenantGetMembersDto.toDomain(): TenantGetMembers {
    return TenantGetMembers(
        members = this.members,
    )
}