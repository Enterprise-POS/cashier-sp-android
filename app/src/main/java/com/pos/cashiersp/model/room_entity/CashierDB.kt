package com.pos.cashiersp.model.room_entity

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [StoreStockEntity::class, CashierItemEntity::class],
    version = 1
)
abstract class CashierDB : RoomDatabase() {
    abstract val storeStockDao: StoreStockDao

    abstract val cashierItemDao: CashierItemDao

    companion object {
        const val DATABASE_NAME = "cashier_db"
    }
}