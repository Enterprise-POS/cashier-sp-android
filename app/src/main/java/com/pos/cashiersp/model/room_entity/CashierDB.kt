package com.pos.cashiersp.model.room_entity

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [StoreStockEntity::class, CashierItemEntity::class, DatabaseCacheMetadataEntity::class],
    version = 7
)
abstract class CashierDB : RoomDatabase() {
    abstract val storeStockDao: StoreStockDao

    abstract val cashierItemDao: CashierItemDao
    abstract val databaseCacheMetadataDao: DatabaseCacheMetadataDao

    companion object {
        const val DATABASE_NAME = "cashier_db"
    }
}