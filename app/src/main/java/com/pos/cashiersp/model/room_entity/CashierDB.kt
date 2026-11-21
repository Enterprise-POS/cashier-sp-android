package com.pos.cashiersp.model.room_entity

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        StoreStockEntity::class,
        CashierItemEntity::class,
        DatabaseCacheMetadataEntity::class,
        VoucherEntity::class,
    ],
    version = 8
)
abstract class CashierDB : RoomDatabase() {
    abstract val storeStockDao: StoreStockDao

    abstract val cashierItemDao: CashierItemDao
    abstract val voucherDao: VoucherDao
    abstract val databaseCacheMetadataDao: DatabaseCacheMetadataDao

    companion object {
        const val DATABASE_NAME = "cashier_db"

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `VoucherEntity` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `voucherId` INTEGER NOT NULL,
                        `code` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `tenantId` INTEGER NOT NULL,
                        `createdAt` TEXT,
                        `voucherType` TEXT NOT NULL,
                        `usedCount` INTEGER NOT NULL,
                        `usageLimit` INTEGER NOT NULL,
                        `startAt` TEXT,
                        `endAt` TEXT,
                        `percentage` INTEGER NOT NULL,
                        `maximumDecrement` INTEGER NOT NULL,
                        `roundingPolicy` TEXT NOT NULL,
                        `decrement` INTEGER NOT NULL,
                        `itemIdCombinations` TEXT NOT NULL,
                        `itemQuantities` INTEGER NOT NULL,
                        `storeId` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("UPDATE databasecachemetadataentity SET lastUpdated = 0")
            }
        }
    }
}