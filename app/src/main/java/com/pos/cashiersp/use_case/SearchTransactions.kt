package com.pos.cashiersp.use_case

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.DateFilter
import com.pos.cashiersp.model.dto.QueryFilter
import com.pos.cashiersp.model.dto.SearchTransactionsDto
import com.pos.cashiersp.presentation.util.SearchTransactionsRequestBody
import com.pos.cashiersp.repository.OrderItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class SearchTransactions(private val repository: OrderItemRepository) {
    operator fun invoke(
        page: Int,
        limit: Int,
        tenantId: Int,
        storeId: Int,
        queryFilter: QueryFilter,
        dateFilter: DateFilter
    ): Flow<Resource<SearchTransactionsDto>> =
        flow {
            try {
                emit(Resource.Loading<SearchTransactionsDto>())
                val response = repository.searchTransactions(
                    searchTransactionsRequestBody = SearchTransactionsRequestBody(
                        page = page,
                        limit = limit,
                        tenantId = tenantId,
                        storeId = storeId,
                        dateFilter = dateFilter,
                        filters = listOf(queryFilter) // Only accept 1 filter and this moment
                    )
                )
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

                // 200
                var transactionResponse: SearchTransactionsDto = successResponse.data
                emit(Resource.Success<SearchTransactionsDto>(transactionResponse))
            } catch (e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] An unexpected error occurred"))
            } catch (e: IOException) {
                println("IOException message: ${e.message}")
                emit(Resource.Error("[INTERNAL ERROR] Couldn't reach server. Check your internet connection."))
            }
        }
}