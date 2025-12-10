package com.pos.cashiersp.use_case

import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.presentation.util.JwtStore
import com.pos.cashiersp.presentation.util.MyCookieImpl
import com.pos.cashiersp.presentation.util.UserPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class IsLoggedIn(private val jwtStore: JwtStore, private val myCookieImpl: MyCookieImpl) {
    operator fun invoke(): Flow<Resource<UserPayload>> = flow {
        // emit(Resource.Loading<UserPayload>())
        val payload = jwtStore.getPayload().first()
        if (payload == null) {
            emit(Resource.Error("[UNAUTHORIZED] User not logged in"))
            return@flow
        }

        if (payload.token.isEmpty() || payload.sub <= 0) {
            emit(Resource.Error("[UNAUTHORIZED] User not logged in"))
            return@flow
        }
        /*
            Save the cookie, so the next request to the server
            then the retrofit already knew what cookie should be use
        * */
        myCookieImpl.restoreCookie(payload.token)

        // User is logged in
        emit(Resource.Success<UserPayload>(payload))
    }
}