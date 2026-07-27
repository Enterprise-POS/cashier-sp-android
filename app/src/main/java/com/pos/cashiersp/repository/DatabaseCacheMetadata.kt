package com.pos.cashiersp.repository

import com.pos.cashiersp.model.room_entity.DatabaseCacheMetadataEntity

interface DatabaseCacheMetadataRepository {
    suspend fun writeMetadata(entity: DatabaseCacheMetadataEntity): Long
    suspend fun getMetadata(storeId: Int, tenantId: Int): DatabaseCacheMetadataEntity?
    suspend fun writeAndReturnMetadata(entity: DatabaseCacheMetadataEntity): DatabaseCacheMetadataEntity
}