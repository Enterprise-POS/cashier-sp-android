package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.GetTenantWithUserDto
import com.pos.cashiersp.model.dto.Tenant
import com.pos.cashiersp.model.dto.TenantGetMembersDto
import okhttp3.ResponseBody
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

    /*
    Fresh new tenant, with current user as a owner
    only create 1 tenant, will call new_tenant_user_as_owner function

    The response will return text with response code 201
    */
    suspend fun newTenant(tenant: Tenant): Response<ResponseBody>
}