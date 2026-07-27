package com.pos.cashiersp.model.room_entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    tableName = "databasecachemetadataentity",
    indices = [Index(value = ["storeId", "tenantId"], unique = true)]
)
data class DatabaseCacheMetadataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lastUpdated: Long, // Using Long considered relatively easier rather than Calendar

    // This will make current cashier item is actually a correct from
    // current tenantID or storeID
    val tenantId: Int,
    val storeId: Int,
)