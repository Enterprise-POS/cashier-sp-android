package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.GetAllStoreDto
import retrofit2.Response

interface StoreRepository {
    /*
        Get All store, and filter available either active / non active only
    */
    suspend fun getAll(
        tenantId: Int,
        page: Int = 1,
        limit: Int = 5,
        includeNonActive: Boolean = false
    ): Response<HTTPStatus.SuccessResponse<GetAllStoreDto>>
}