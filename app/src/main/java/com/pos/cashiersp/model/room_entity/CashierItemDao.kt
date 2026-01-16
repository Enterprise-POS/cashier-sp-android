package com.pos.cashiersp.model.room_entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CashierItemDao {


    @Query("SELECT * FROM cashieritementity WHERE storeId = :storeId AND tenantId = :tenantId")
    fun getCashierItems(tenantId: Int, storeId: Int): Flow<List<CashierItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashierItems(cashierItems: List<CashierItemEntity>)

    @Query("DELETE FROM cashieritementity WHERE tenantId = :tenantId AND storeId = :storeId")
    suspend fun deleteByTenantAndStore(tenantId: Int, storeId: Int)

    @Query("DELETE FROM cashieritementity")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM cashieritementity WHERE tenantId = :tenantId AND storeId = :storeId")
    suspend fun getCount(tenantId: Int, storeId: Int): Int
}