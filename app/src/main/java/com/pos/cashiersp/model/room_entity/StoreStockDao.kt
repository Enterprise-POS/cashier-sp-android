package com.pos.cashiersp.model.room_entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreStockDao {

    @Query("SELECT * FROM storestockentity WHERE storeId=:storeId")
    fun getStoreStockByStoreId(storeId: Int): Flow<List<StoreStockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoreStock(storeStock: StoreStockEntity)

    @Query("DELETE FROM storestockentity")
    suspend fun deleteAll()
}