package com.pos.cashiersp.use_case

import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.presentation.util.JwtStore
import com.pos.cashiersp.presentation.util.UserPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class IsLoggedIn(private val jwtStore: JwtStore) {
    operator fun invoke(): Flow<Resource<UserPayload>> = flow {
        emit(Resource.Loading<UserPayload>())
        val payload = jwtStore.getPayload().first()
        if (payload == null) {
            emit(Resource.Error("[UNAUTHORIZED] User not logged in"))
            return@flow
        }
        if (payload.token.isEmpty() || payload.sub == 0) {
            emit(Resource.Error("[UNAUTHORIZED] User not logged in"))
            return@flow
        }
        emit(Resource.Success<UserPayload>(payload))
    }
}