package com.pos.cashiersp.use_case

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.CashierDataDto
import com.pos.cashiersp.model.dto.CashierItem
import com.pos.cashiersp.model.dto.toDBEntity
import com.pos.cashiersp.model.room_entity.CashierItemEntity
import com.pos.cashiersp.repository.StoreStockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class LoadCashierData(private val repository: StoreStockRepository) {
    operator fun invoke(tenantId: Int, storeId: Int): Flow<Resource<List<CashierItem>>> = flow {
        try {
            emit(Resource.Loading<List<CashierItem>>())
            if (tenantId < 1) {
                emit(Resource.Error("Invalid tenant id value"))
                return@flow
            }
            if (storeId < 1) {
                emit(Resource.Error("Invalid store id value"))
                return@flow
            }

            val response = repository.loadCashierData(tenantId, storeId)
            if (!response.isSuccessful) {
                when (response.code()) {
                    400, 401, 403 -> {
                        val reader = response.errorBody()!!.charStream()
                        val type = object : TypeToken<HTTPStatus.ErrorResponse>() {}.type
                        val errorResponse = Gson().fromJson<HTTPStatus.ErrorResponse>(reader, type)
                        emit(Resource.Error(errorResponse.message))
                        return@flow
                    }

                    else -> {
                        println("[INTERNAL ERROR] ${response.message()}")
                        println(response.errorBody())
                        emit(Resource.Error("[INTERNAL ERROR] Application Crashed"))
                        return@flow
                    }
                }
            }

            val successResponse = response.body()
            if (successResponse == null) {
                emit(Resource.Error("[FATAL ERROR] Empty JSON body"))
                return@flow
            }

            // 200 ok
            var cashierDataDto: CashierDataDto = successResponse.data

            try {
                // Clear old data for this store
                repository.deleteCashierItems(tenantId, storeId)

                // Batch insert all items at once
                val entities: List<CashierItemEntity> =
                    cashierDataDto.cashierData.map { it.toDBEntity(tenantId, storeId) }
                repository.insertCashierItems(entities)

                // println("Successfully cached ${entities.size} items")
            } catch (dbError: Exception) {
                println("Failed to cache items: ${dbError.message}")
                // Don't fail the whole operation if caching fails
            }

            emit(Resource.Success<List<CashierItem>>(cashierDataDto.cashierData))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] An unexpected error occurred"))
        } catch (e: IOException) {
            println("IOException message: ${e.message}")
            emit(Resource.Error("[INTERNAL ERROR] Couldn't reach server. Check your internet connection."))
        }
    }
}