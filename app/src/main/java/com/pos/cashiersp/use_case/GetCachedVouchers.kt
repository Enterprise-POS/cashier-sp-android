package com.pos.cashiersp.use_case

import android.database.sqlite.SQLiteException
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.room_entity.VoucherEntity
import com.pos.cashiersp.repository.StoreStockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class GetCachedVouchers(private val repository: StoreStockRepository) {
    operator fun invoke(tenantId: Int, storeId: Int): Flow<Resource<List<VoucherEntity>>> = flow {
        emit(Resource.Loading())
        try {
            val vouchers = repository.getCachedVouchers(tenantId, storeId).firstOrNull()
            if (vouchers == null) {
                emit(Resource.Error("Error returning cached vouchers"))
            } else {
                emit(Resource.Success(vouchers))
            }
        } catch (e: SQLiteException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] Failed to read cached vouchers"))
        }
    }
}
