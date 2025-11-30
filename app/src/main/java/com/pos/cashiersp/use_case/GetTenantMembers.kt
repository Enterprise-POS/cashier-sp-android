package com.pos.cashiersp.use_case

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.common.error
import com.pos.cashiersp.model.domain.TenantGetMembers
import com.pos.cashiersp.model.dto.TenantGetMembersDto
import com.pos.cashiersp.model.dto.toDomain
import com.pos.cashiersp.repository.TenantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class GetTenantMembers (private val repository: TenantRepository) {
    operator fun invoke(tenantId: Int): Flow<Resource<TenantGetMembers>> = flow {
        try {
            emit(Resource.Loading<TenantGetMembers>())
            if (tenantId < 1) {
                emit(Resource.Error("Invalid tenant id value"))
                return@flow
            }

            val response = repository.getTenantMembers(tenantId)
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
            var tenantGetMembers = successResponse.data.toDomain()
            emit(Resource.Success<TenantGetMembers>(tenantGetMembers))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] An unexpected error occurred"))
        } catch(e: IOException) {
            println("IOException message: ${e.message}")
            emit(Resource.Error("[INTERNAL ERROR] Couldn't reach server. Check your internet connection."))
        }
    }
}