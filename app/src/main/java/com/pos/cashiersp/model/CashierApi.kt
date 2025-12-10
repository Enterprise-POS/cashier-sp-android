package com.pos.cashiersp.model

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.GetTenantWithUserDto
import com.pos.cashiersp.model.dto.LoginResponseDto
import com.pos.cashiersp.model.dto.SignUpResponseDto
import com.pos.cashiersp.model.dto.TenantGetMembersDto
import com.pos.cashiersp.presentation.util.LoginRequest
import com.pos.cashiersp.presentation.util.SignUpWithEmailAndPasswordRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CashierApi {
    // Tenant
    @GET("tenants/members/{tenantId}")
    suspend fun getTenantMembers(@Path("tenantId") tenantId: String)
            : Response<HTTPStatus.SuccessResponse<TenantGetMembersDto>>

    @GET("tenants/{tenantId}")
    suspend fun getTenantWithUser(@Path("tenantId") tenantId: String):
            Response<HTTPStatus.SuccessResponse<GetTenantWithUserDto>>

    // User
    @POST("users/sign_in")
    suspend fun loginRequest(@Body loginRequest: LoginRequest)
            : Response<HTTPStatus.SuccessResponse<LoginResponseDto>>

    @POST("users/sign_up")
    suspend fun signUpWithEmailAndPassword(@Body signUpWithEmailAndPassword: SignUpWithEmailAndPasswordRequestBody)
            : Response<HTTPStatus.SuccessResponse<SignUpResponseDto>>

}