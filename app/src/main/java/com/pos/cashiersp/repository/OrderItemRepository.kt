package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.CreateTransactionParams
import com.pos.cashiersp.model.dto.TransactionResponse
import retrofit2.Response

interface OrderItemRepository {
    suspend fun transactions(
        createTransactionParams: CreateTransactionParams,
        tenantId: Int
    ): Response<HTTPStatus.SuccessResponse<TransactionResponse>>
}