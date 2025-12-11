package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.model.dto.LoginResponseDto
import com.pos.cashiersp.model.dto.SignUpResponseDto
import com.pos.cashiersp.presentation.util.LoginRequestBody
import com.pos.cashiersp.presentation.util.SignUpWithEmailAndPasswordRequestBody
import retrofit2.Response
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val api: CashierApi) : UserRepository {
    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Response<HTTPStatus.SuccessResponse<LoginResponseDto>> {
        return api.loginRequest(LoginRequestBody(email, password))
    }

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        name: String
    ): Response<HTTPStatus.SuccessResponse<SignUpResponseDto>> {
        return api.signUpWithEmailAndPassword(
            SignUpWithEmailAndPasswordRequestBody(email, password, name)
        )
    }
}