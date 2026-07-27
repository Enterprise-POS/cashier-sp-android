package com.pos.cashiersp.repository

import com.pos.cashiersp.model.room_entity.DatabaseCacheMetadataDao
import com.pos.cashiersp.model.room_entity.DatabaseCacheMetadataEntity

class DatabaseCacheMetadataImpl(private val dao: DatabaseCacheMetadataDao) : DatabaseCacheMetadataRepository {
    override suspend fun writeMetadata(entity: DatabaseCacheMetadataEntity): Long {
        dao.writeMetadata(entity)
        return entity.lastUpdated
    }

    override suspend fun getMetadata(storeId: Int, tenantId: Int): DatabaseCacheMetadataEntity? {
        return dao.getMetadata(storeId, tenantId)
    }

    override suspend fun writeAndReturnMetadata(entity: DatabaseCacheMetadataEntity): DatabaseCacheMetadataEntity {
        return dao.writeAndReturnMetadata(entity)
    }
}