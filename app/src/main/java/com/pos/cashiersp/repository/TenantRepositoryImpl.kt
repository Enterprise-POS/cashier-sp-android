package com.pos.cashiersp.repository

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.model.domain.TenantGetMembers
import com.pos.cashiersp.model.dto.TenantGetMembersDto
import com.pos.cashiersp.model.dto.toDomain
import retrofit2.Response
import javax.inject.Inject

class TenantRepositoryImpl @Inject constructor(private val api: CashierApi): TenantRepository {
    override suspend fun getTenantMembers(tenantId: Int): Response<HTTPStatus.SuccessResponse<TenantGetMembersDto>> {
        return api.getTenantMembers(tenantId.toString())
    }
}
