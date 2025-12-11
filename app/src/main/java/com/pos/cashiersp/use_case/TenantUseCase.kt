package com.pos.cashiersp.use_case

data class TenantUseCase(
    val getTenantMembers: GetTenantMembers,
    val getTenantWithUser: GetTenantWithUser,
    val newTenant: NewTenant,
)