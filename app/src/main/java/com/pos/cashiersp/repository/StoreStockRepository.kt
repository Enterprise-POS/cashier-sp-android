package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.CashierDataDto
import com.pos.cashiersp.model.dto.CreateTransactionParams
import com.pos.cashiersp.model.dto.StoreStockGetV2Dto
import com.pos.cashiersp.model.dto.TransactionResponse
import com.pos.cashiersp.model.room_entity.CashierItemEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface StoreStockRepository {
    suspend fun getV2(
        tenantId: Int,
        storeId: Int,
        limit: Int = 5,
        page: Int = 1,
        nameQuery: String = "",
    ): Response<HTTPStatus.SuccessResponse<StoreStockGetV2Dto>>

    suspend fun loadCashierData(tenantId: Int, storeId: Int): Response<HTTPStatus.SuccessResponse<CashierDataDto>>

    fun getCachedCashierItems(tenantId: Int, storeId: Int): Flow<List<CashierItemEntity>>
    suspend fun insertCashierItems(cashierItemEntities: List<CashierItemEntity>)
    suspend fun deleteCashierItems(tenantId: Int, storeId: Int)
}