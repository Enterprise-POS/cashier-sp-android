package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.model.dto.CreateTransactionParams
import com.pos.cashiersp.model.dto.FindTransactionsByIdDto
import com.pos.cashiersp.model.dto.SearchTransactionsDto
import com.pos.cashiersp.model.dto.TransactionResponse
import com.pos.cashiersp.model.dto.request_body.CancelTransactionBody
import com.pos.cashiersp.model.dto.response_body.PaymentStatusResponse
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

    override suspend fun findTransactionsById(
        id: Int,
        tenantId: Int
    ): Response<HTTPStatus.SuccessResponse<FindTransactionsByIdDto>> {
        return api.findTransactionsById(tenantId, id)
    }

    override suspend fun checkTransactionStatus(
        tenantId: Int,
        orderItemId: Int,
        transactionId: String,
    ): Response<HTTPStatus.SuccessResponse<PaymentStatusResponse>> {
        return api.checkTransactionStatus(tenantId, orderItemId, transactionId)
    }

    override suspend fun cancelTransaction(
        tenantId: Int,
        orderItemId: Int,
        transactionId: String
    ): Response<HTTPStatus.SuccessResponse<PaymentStatusResponse>> {
        return api.cancelTransaction(tenantId, CancelTransactionBody(orderItemId, transactionId))
    }
}