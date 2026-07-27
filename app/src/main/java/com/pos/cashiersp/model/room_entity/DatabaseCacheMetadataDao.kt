package com.pos.cashiersp.model.room_entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DatabaseCacheMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun writeMetadata(cashierItems: DatabaseCacheMetadataEntity): Long

    @Query("SELECT * FROM databasecachemetadataentity WHERE storeId = :storeId AND tenantId = :tenantId LIMIT 1")
    suspend fun getMetadata(storeId: Int, tenantId: Int): DatabaseCacheMetadataEntity?

    @Transaction
    suspend fun writeAndReturnMetadata(entity: DatabaseCacheMetadataEntity): DatabaseCacheMetadataEntity {
        writeMetadata(entity)
        return getMetadata(entity.storeId, entity.tenantId)!!
    }
}