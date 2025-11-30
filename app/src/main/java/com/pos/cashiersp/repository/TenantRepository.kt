package com.pos.cashiersp.repository

import com.google.gson.JsonElement
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.TenantGetMembersDto
import retrofit2.Response

interface TenantRepository {
    /*
    Get 1 tenant users/members
    */
    suspend fun getTenantMembers(tenantId: Int): Response<HTTPStatus.SuccessResponse<TenantGetMembersDto>>
}