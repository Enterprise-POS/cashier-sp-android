package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.model.dto.GetTenantWithUserDto
import com.pos.cashiersp.model.dto.Tenant
import com.pos.cashiersp.model.dto.TenantGetMembersDto
import com.pos.cashiersp.presentation.util.NewTenantRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class TenantRepositoryImpl @Inject constructor(private val api: CashierApi) : TenantRepository {
    override suspend fun getTenantMembers(tenantId: Int): Response<HTTPStatus.SuccessResponse<TenantGetMembersDto>> {
        return api.getTenantMembers(tenantId.toString())
    }

    override suspend fun getTenantWithUser(userId: Int): Response<HTTPStatus.SuccessResponse<GetTenantWithUserDto>> {
        return api.getTenantWithUser(userId.toString())
    }

    override suspend fun newTenant(tenant: Tenant): Response<ResponseBody> {
        return api.newTenant(NewTenantRequestBody(tenant.name, tenant.ownerUserId))
    }
}
