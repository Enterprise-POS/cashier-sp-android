package com.pos.cashiersp.repository

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.LoginResponseDto
import com.pos.cashiersp.model.dto.SignUpResponseDto
import retrofit2.Response

interface UserRepository {
    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Response<HTTPStatus.SuccessResponse<LoginResponseDto>>

    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        username: String,
    ): Response<HTTPStatus.SuccessResponse<SignUpResponseDto>>
}