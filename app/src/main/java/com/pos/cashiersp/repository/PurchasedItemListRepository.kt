package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.PurchasedItemDto
import com.pos.cashiersp.model.dto.response_body.PurchasedItemListLogsResponse
import com.pos.cashiersp.presentation.util.PurchasedItemListLogsRequestBody
import retrofit2.Response

interface PurchasedItemListRepository {
    suspend fun purchasedItemListLogs(
        body: PurchasedItemListLogsRequestBody,
        tenantId: Int
    ): Response<HTTPStatus.SuccessResponse<PurchasedItemListLogsResponse>>
}