package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.GetTenantWithUserDto
import com.pos.cashiersp.model.dto.TenantGetMembersDto
import retrofit2.Response

interface TenantRepository {
    /*
    Get 1 tenant users/members
    */
    suspend fun getTenantMembers(tenantId: Int): Response<HTTPStatus.SuccessResponse<TenantGetMembersDto>>

    /*
    Return all tenants from 1 user tenant
    - will call user_mtm_tenant
    */
    suspend fun getTenantWithUser(userId: Int): Response<HTTPStatus.SuccessResponse<GetTenantWithUserDto>>
}