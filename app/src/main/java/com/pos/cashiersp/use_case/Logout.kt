package com.pos.cashiersp.use_case

import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.presentation.util.JwtStore
import com.pos.cashiersp.presentation.util.MyCookieImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Logout(private val jwtStore: JwtStore, private val myCookieImpl: MyCookieImpl) {
    operator fun invoke(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        jwtStore.clearToken()
        myCookieImpl.clear()
        emit(Resource.Success<Boolean>(true))
    }
}