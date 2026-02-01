package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.CreateTransactionParams
import com.pos.cashiersp.model.dto.DateFilter
import com.pos.cashiersp.model.dto.QueryFilter
import com.pos.cashiersp.model.dto.SearchTransactionsDto
import com.pos.cashiersp.model.dto.TransactionResponse
import com.pos.cashiersp.presentation.util.SearchTransactionsRequestBody
import retrofit2.Response

interface OrderItemRepository {
    suspend fun transactions(
        createTransactionParams: CreateTransactionParams,
        tenantId: Int
    ): Response<HTTPStatus.SuccessResponse<TransactionResponse>>

    suspend fun searchTransactions(
        searchTransactionsRequestBody: SearchTransactionsRequestBody
    ): Response<HTTPStatus.SuccessResponse<SearchTransactionsDto>>
}