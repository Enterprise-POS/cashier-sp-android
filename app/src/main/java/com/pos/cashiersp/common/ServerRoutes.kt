package com.pos.cashiersp.common

// Because using retrofit, and set retrofit base url, then
// the base url is not needed
object ServerRoutes {
    const val GET_TENANT_MEMBERS = "tenants/members/{tenantId}"
}