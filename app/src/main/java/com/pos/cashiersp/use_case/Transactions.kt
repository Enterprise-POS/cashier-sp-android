package com.pos.cashiersp.use_case

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.CreateTransactionParams
import com.pos.cashiersp.model.dto.TransactionResponse
import com.pos.cashiersp.repository.OrderItemRepository
import com.pos.cashiersp.repository.StoreStockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class Transactions(private val repository: OrderItemRepository) {
    /*
        Use -> CreateTransactionParams
        Transaction api expect body as:
        {
            "purchased_price": 3000,
            "total_quantity":  2,
            "total_amount":    3000,
            "discount_amount": 0,
            "sub_total":       3000,

            "items": [
                {
                    "item_id":         1229,
                    "quantity":        2,
                    "purchased_price": 1500,
                    "discount_amount": 0,
                    "total_amount":    3000
                }
            ],

            "user_id":   167,
            "tenant_id": 333,
            "store_id":  141
        }
     */
    operator fun invoke(createTransactionParams: CreateTransactionParams): Flow<Resource<TransactionResponse>> = flow {
        try {
            emit(Resource.Loading<TransactionResponse>())
            val tenantId = createTransactionParams.tenantId
            val storeId = createTransactionParams.storeId
            val totalAmount = createTransactionParams.totalAmount
            val purchasedPrice = createTransactionParams.purchasedPrice
            if (tenantId < 1) {
                emit(Resource.Error("Fatal error ! Invalid value. \ntenantId: $tenantId"))
                return@flow
            }
            if (storeId < 1) {
                emit(Resource.Error("Fatal error ! Invalid value. \nstoreId: $storeId"))
                return@flow
            }
            if (purchasedPrice < totalAmount) {
                emit(Resource.Error("Insufficient balance. Please check again balance before transaction"))
                return@flow
            }

            val response = repository.transactions(createTransactionParams, tenantId)
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
            var transactionResponse: TransactionResponse = successResponse.data
            emit(Resource.Success<TransactionResponse>(transactionResponse))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] An unexpected error occurred"))
        } catch (e: IOException) {
            println("IOException message: ${e.message}")
            emit(Resource.Error("[INTERNAL ERROR] Couldn't reach server. Check your internet connection."))
        }
    }
}