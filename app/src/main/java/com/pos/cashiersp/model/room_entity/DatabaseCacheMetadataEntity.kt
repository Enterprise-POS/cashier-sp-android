package com.pos.cashiersp.model.room_entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class DatabaseCacheMetadataEntity(
    @PrimaryKey val id: Int,
    val lastUpdated: Long, // Using Long considered relatively easier rather than Calendar

    // This will make current cashier item is actually a correct from
    // current tenantID or storeID
    val tenantId: Int,
    val storeId: Int,
)