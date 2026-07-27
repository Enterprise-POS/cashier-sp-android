package com.pos.cashiersp.use_case

import android.database.sqlite.SQLiteException
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.room_entity.DatabaseCacheMetadataEntity
import com.pos.cashiersp.repository.DatabaseCacheMetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class GetMetadata(private val repository: DatabaseCacheMetadataRepository) {
    operator fun invoke(storeId: Int, tenantId: Int): Flow<Resource<DatabaseCacheMetadataEntity?>> = flow {
        emit(Resource.Loading())
        try {
            val result: DatabaseCacheMetadataEntity? = repository.getMetadata(storeId, tenantId)
            emit(Resource.Success(result))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] An unexpected error occurred"))
        } catch (e: IOException) {
            println("IOException message: ${e.message}")
            emit(Resource.Error("[INTERNAL ERROR] Couldn't reach server. Check your internet connection."))
        } catch (e: SQLiteException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] Failed to write cache metadata"))
        }
    }
}