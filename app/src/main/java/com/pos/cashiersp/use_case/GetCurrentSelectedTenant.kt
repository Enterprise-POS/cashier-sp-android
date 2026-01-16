package com.pos.cashiersp.use_case

import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.presentation.util.SelectedTenantDS
import com.pos.cashiersp.presentation.util.SelectedTenantDSPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GetCurrentSelectedTenant(private val selectedTenant: SelectedTenantDS) {
    operator fun invoke(): Flow<Resource<SelectedTenantDSPayload>> = flow {
        try {
            emit(Resource.Loading())
            val selectedTenantPayload = selectedTenant.getPayload().first()
            if (selectedTenantPayload == null) {
                emit(Resource.Error("[UNSELECTED] No tenant selected"))
                return@flow
            }

            emit(Resource.Success<SelectedTenantDSPayload>(selectedTenantPayload))
        } catch (exception: Exception) {
            println("Exception error at GetCurrentSelectedTenant: ${exception.message}")
            emit(Resource.Error(exception.message ?: "Unknown error detected"))
        }
    }
}