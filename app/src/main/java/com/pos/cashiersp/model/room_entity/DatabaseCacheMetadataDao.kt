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

    // Always at top / id always 1
    @Query("SELECT * FROM databasecachemetadataentity WHERE id = 1 LIMIT 1")
    suspend fun getMetadata(): DatabaseCacheMetadataEntity?

    @Transaction
    suspend fun writeAndReturnMetadata(entity: DatabaseCacheMetadataEntity): DatabaseCacheMetadataEntity {
        writeMetadata(entity)
        return getMetadata()!!
    }
}