package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.model.dto.CashierDataDto
import com.pos.cashiersp.model.dto.StoreStockGetV2Dto
import com.pos.cashiersp.model.room_entity.CashierItemDao
import com.pos.cashiersp.model.room_entity.CashierItemEntity
import com.pos.cashiersp.model.room_entity.VoucherDao
import com.pos.cashiersp.model.room_entity.VoucherEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class StoreStockRepositoryImpl(
    private val api: CashierApi,
    private val dao: CashierItemDao,
    private val voucherDao: VoucherDao,
) : StoreStockRepository {
    override suspend fun getV2(
        tenantId: Int,
        storeId: Int,
        limit: Int,
        page: Int,
        nameQuery: String
    ): Response<HTTPStatus.SuccessResponse<StoreStockGetV2Dto>> {
        return api.storeStockGetV2(
            tenantId,
            page = page,
            limit = limit,
            storeId = storeId,
            nameQuery = nameQuery
        )
    }

    override suspend fun loadCashierData(
        tenantId: Int,
        storeId: Int
    ): Response<HTTPStatus.SuccessResponse<CashierDataDto>> {
        return api.loadCashierData(tenantId, storeId)
    }

    override fun getCachedCashierItems(
        tenantId: Int,
        storeId: Int
    ): Flow<List<CashierItemEntity>> {
        return dao.getCashierItems(tenantId, storeId)
    }

    override suspend fun insertCashierItems(cashierItemEntities: List<CashierItemEntity>) {
        return dao.insertCashierItems(cashierItemEntities)
    }

    override suspend fun deleteCashierItems(tenantId: Int, storeId: Int) {
        dao.deleteByTenantAndStore(tenantId, storeId)
    }

    override fun getCachedVouchers(tenantId: Int, storeId: Int): Flow<List<VoucherEntity>> {
        return voucherDao.getVouchers(tenantId, storeId)
    }

    override suspend fun insertVouchers(vouchers: List<VoucherEntity>) {
        voucherDao.insertVouchers(vouchers)
    }

    override suspend fun deleteVouchers(tenantId: Int, storeId: Int) {
        voucherDao.deleteByTenantAndStore(tenantId, storeId)
    }
}