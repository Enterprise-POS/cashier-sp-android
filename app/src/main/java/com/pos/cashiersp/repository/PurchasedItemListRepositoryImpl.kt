package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.model.dto.response_body.PurchasedItemListLogsResponse
import com.pos.cashiersp.presentation.util.PurchasedItemListLogsRequestBody
import retrofit2.Response

class PurchasedItemListRepositoryImpl(private val api: CashierApi) : PurchasedItemListRepository {
    override suspend fun purchasedItemListLogs(
        body: PurchasedItemListLogsRequestBody,
        tenantId: Int
    ): Response<HTTPStatus.SuccessResponse<PurchasedItemListLogsResponse>> {
        return api.purchasedItemListLogs(body, tenantId)
    }
}