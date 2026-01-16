package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.model.dto.GetAllStoreDto
import retrofit2.Response

class StoreRepositoryImpl(private val api: CashierApi) : StoreRepository {
    override suspend fun getAll(
        tenantId: Int,
        page: Int,
        limit: Int,
        includeNonActive: Boolean
    ): Response<HTTPStatus.SuccessResponse<GetAllStoreDto>> {
        return api.storeGetAll(tenantId, page, limit, includeNonActive)
    }
}