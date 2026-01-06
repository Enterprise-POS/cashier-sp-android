package com.pos.cashiersp.use_case

import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.presentation.util.SelectedTenantDS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UnselectTenant(private val selectedTenantDS: SelectedTenantDS) {
    operator fun invoke(): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            selectedTenantDS.unselectAnything()
            emit(Resource.Success(Unit))
        } catch (exception: Exception) {
            println("Exception error at UnselectTenant: ${exception.message}")
            emit(Resource.Error(exception.message ?: "Unknown error detected"))
        }
    }
}