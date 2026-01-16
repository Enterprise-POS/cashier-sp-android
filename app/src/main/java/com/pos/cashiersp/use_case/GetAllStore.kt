package com.pos.cashiersp.use_case

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.GetAllStoreDto
import com.pos.cashiersp.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class GetAllStore(private val storeRepository: StoreRepository) {
    operator fun invoke(
        tenantId: Int,
        page: Int,
        limit: Int,
        includeNonActive: Boolean
    ): Flow<Resource<GetAllStoreDto>> = flow {
        try {
            emit(Resource.Loading<GetAllStoreDto>())
            if (tenantId < 1) {
                emit(Resource.Error("Invalid tenant id value. Please restart the app."))
                return@flow
            }

            val response = storeRepository.getAll(tenantId, page, limit, includeNonActive)
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
            var getAllStoreDto = successResponse.data
            emit(Resource.Success<GetAllStoreDto>(getAllStoreDto))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] An unexpected error occurred"))
        } catch (e: IOException) {
            println("IOException message: ${e.message}")
            emit(Resource.Error("[INTERNAL ERROR] Couldn't reach server. Check your internet connection."))
        }
    }
}