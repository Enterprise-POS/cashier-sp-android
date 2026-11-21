package com.pos.cashiersp.model.room_entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VoucherDao {
    @Query("SELECT * FROM voucherentity WHERE tenantId = :tenantId AND storeId = :storeId")
    fun getVouchers(tenantId: Int, storeId: Int): Flow<List<VoucherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVouchers(vouchers: List<VoucherEntity>)

    @Query("DELETE FROM voucherentity WHERE tenantId = :tenantId AND storeId = :storeId")
    suspend fun deleteByTenantAndStore(tenantId: Int, storeId: Int)
}
