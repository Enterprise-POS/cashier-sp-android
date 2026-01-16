package com.pos.cashiersp.use_case

import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.presentation.util.SelectedStoreDS
import com.pos.cashiersp.presentation.util.SelectedStoreDSPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GetCurrentSelectedStore(private val selectedStoreDS: SelectedStoreDS) {
    operator fun invoke(): Flow<Resource<SelectedStoreDSPayload>> = flow {
        try {
            emit(Resource.Loading())
            val selectedStorePayload = selectedStoreDS.getPayload().first()
            if (selectedStorePayload == null) {
                emit(Resource.Error("[UNSELECTED] No store selected"))
                return@flow
            }
            emit(Resource.Success(selectedStorePayload))
        } catch (exception: Exception) {
            println("Exception error at UnselectTenant: ${exception.message}")
            emit(Resource.Error(exception.message ?: "Unknown error detected"))
        }
    }
}