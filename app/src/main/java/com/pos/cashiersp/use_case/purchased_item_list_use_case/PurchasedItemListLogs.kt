package com.pos.cashiersp.use_case.purchased_item_list_use_case

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.PurchasedItemDto
import com.pos.cashiersp.model.dto.response_body.PurchasedItemListLogsResponse
import com.pos.cashiersp.presentation.util.PurchasedItemListLogsRequestBody
import com.pos.cashiersp.repository.PurchasedItemListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class PurchasedItemListLogs(private val repository: PurchasedItemListRepository) {
    operator fun invoke(
        body: PurchasedItemListLogsRequestBody,
        tenantId: Int
    ): Flow<Resource<PurchasedItemListLogsResponse>> =
        flow {
            try {
                emit(Resource.Loading<PurchasedItemListLogsResponse>())
                if (tenantId < 1) {
                    emit(Resource.Error("Fatal error ! Invalid value. \ntenantId: $tenantId"))
                    return@flow
                }
                val response = repository.purchasedItemListLogs(body, tenantId)
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
                val data: PurchasedItemListLogsResponse = successResponse.data
                emit(Resource.Success(data))
            } catch (e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] An unexpected error occurred"))
            } catch (e: IOException) {
                println("IOException message: ${e.message}")
                emit(Resource.Error("[INTERNAL ERROR] Couldn't reach server. Check your internet connection."))
            }
        }
}