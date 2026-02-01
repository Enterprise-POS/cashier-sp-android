package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.model.dto.CreateTransactionParams
import com.pos.cashiersp.model.dto.DateFilter
import com.pos.cashiersp.model.dto.QueryFilter
import com.pos.cashiersp.model.dto.SearchTransactionsDto
import com.pos.cashiersp.model.dto.TransactionResponse
import com.pos.cashiersp.presentation.util.SearchTransactionsRequestBody
import retrofit2.Response

class OrderItemRepositoryImpl(private val api: CashierApi) : OrderItemRepository {
    override suspend fun transactions(
        createTransactionParams: CreateTransactionParams,
        tenantId: Int
    ): Response<HTTPStatus.SuccessResponse<TransactionResponse>> {
        return api.transactions(createTransactionParams, tenantId)
    }

    override suspend fun searchTransactions(
        searchTransactionsRequestBody: SearchTransactionsRequestBody
    ): Response<HTTPStatus.SuccessResponse<SearchTransactionsDto>> {
        return api.searchTransactions(searchTransactionsRequestBody.tenantId, searchTransactionsRequestBody)
    }
}