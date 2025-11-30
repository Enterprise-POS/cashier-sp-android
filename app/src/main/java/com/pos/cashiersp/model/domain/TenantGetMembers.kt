package com.pos.cashiersp.model.domain

import com.pos.cashiersp.model.dto.Member

data class TenantGetMembers(
    val members: List<Member>
)
