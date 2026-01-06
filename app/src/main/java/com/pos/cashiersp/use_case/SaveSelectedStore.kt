package com.pos.cashiersp.use_case

import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.presentation.util.SelectedStoreDS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SaveSelectedStore(private val selectedStoreDS: SelectedStoreDS) {
    operator fun invoke(id: Int, name: String): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            selectedStoreDS.saveSelectedStore(id, name)
            emit(Resource.Success(Unit))
        } catch (exception: Exception) {
            println("Exception error at SaveSelectedStore: ${exception.message}")
            emit(Resource.Error(exception.message ?: "Unknown error detected"))
        }
    }
}