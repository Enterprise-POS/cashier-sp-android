package com.pos.cashiersp.use_case

import android.database.sqlite.SQLiteException
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.room_entity.CashierItemEntity
import com.pos.cashiersp.model.room_entity.DatabaseCacheMetadataEntity
import com.pos.cashiersp.repository.StoreStockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.lastOrNull
import retrofit2.HttpException
import java.io.IOException

class GetCachedCashierItems(private val repository: StoreStockRepository) {
    operator fun invoke(tenantId: Int, storeId: Int): Flow<Resource<List<CashierItemEntity>>> = flow {
        emit(Resource.Loading())
        try {
            val cachedCashierItems = repository.getCachedCashierItems(tenantId, storeId).firstOrNull()
            if (cachedCashierItems == null) {
                return@flow emit(Resource.Error("Error returning cached items"))
            }
            emit(Resource.Success(cachedCashierItems))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] An unexpected error occurred"))
        } catch (e: IOException) {
            println("IOException message: ${e.message}")
            emit(Resource.Error("[INTERNAL ERROR] Couldn't reach server. Check your internet connection."))
        } catch (e: SQLiteException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] Failed to write cache metadata"))
        }
    }
}