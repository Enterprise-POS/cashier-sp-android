package com.pos.cashiersp.use_case

import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.room_entity.CashierItemEntity
import com.pos.cashiersp.repository.StoreStockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/*
* Caching cashier item will wipe all the data from local database
* */
class CacheCashierItem(private val repository: StoreStockRepository) {
    operator fun invoke(tenantId: Int, storeId: Int, cashierItems: List<CashierItemEntity>): Flow<Resource<Unit>> =
        flow {
            try {
                emit(Resource.Loading<Unit>())
                // Clear old data for this store
                repository.deleteCashierItems(tenantId, storeId)

                // Batch insert all items at once
                repository.insertCashierItems(cashierItems)

                // println("Successfully cached ${entities.size} items")
            } catch (dbError: Exception) {
                println("Failed to cache items: ${dbError.message}")
                // Don't fail the whole operation if caching fails
            }
        }
}