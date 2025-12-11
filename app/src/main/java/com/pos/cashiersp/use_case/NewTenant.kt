package com.pos.cashiersp.use_case

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.Tenant
import com.pos.cashiersp.presentation.util.JwtStore
import com.pos.cashiersp.repository.TenantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class NewTenant(private val repository: TenantRepository, private val jwtStore: JwtStore) {
    operator fun invoke(name: String): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            if (name.isEmpty() || name.length < 2) {
                emit(Resource.Error("Please fill at least 2 characters"))
                return@flow
            }
            val payload = jwtStore.getPayload().first()
            if (payload == null) {
                emit(Resource.Error("[UNAUTHORIZED] User not logged in"))
                return@flow
            }

            val newTenantCandidate = Tenant(
                name = name,
                ownerUserId = payload.sub,
                createdAt = "",
                id = 0,
                isActive = true,
            )
            val response = repository.newTenant(newTenantCandidate)
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
                        emit(Resource.Error("Application Crashed. Message: ${response.message()}"))
                        return@flow
                    }
                }
            }

            // 201: only return text 'created'
            emit(Resource.Success(Unit))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Internal Error, An unexpected error occurred"))
        } catch (e: IOException) {
            println("[INTERNAL ERROR] IOException message: ${e.message}")
            emit(Resource.Error("Couldn't reach server. Check your internet connection or try again."))
        }
    }
}