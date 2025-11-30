package com.pos.cashiersp.model

import retrofit2.http.GET
import retrofit2.http.Path
import com.google.gson.JsonElement
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.TenantGetMembersDto
import retrofit2.Response

interface CashierApi {
    // Tenant
    @GET("tenants/members/{tenantId}")
    suspend fun getTenantMembers(@Path("tenantId") tenantId: String): Response<HTTPStatus.SuccessResponse<TenantGetMembersDto>>
}