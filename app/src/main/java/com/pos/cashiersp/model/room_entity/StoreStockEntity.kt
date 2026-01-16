package com.pos.cashiersp.model.room_entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StoreStockEntity(
    @PrimaryKey val id: Int,

    val itemId: Int,
    val itemName: String,
    val price: Int,
    val stocks: Int,
    val createdAt: String,
    val lastUpdate: String,
    val storeId: Int
)