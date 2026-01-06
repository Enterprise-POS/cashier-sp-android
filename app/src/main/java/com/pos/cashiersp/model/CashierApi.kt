package com.pos.cashiersp.model

import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.model.dto.CashierDataDto
import com.pos.cashiersp.model.dto.CreateTransactionParams
import com.pos.cashiersp.model.dto.GetAllStoreDto
import com.pos.cashiersp.model.dto.GetTenantWithUserDto
import com.pos.cashiersp.model.dto.LoginResponseDto
import com.pos.cashiersp.model.dto.SignUpResponseDto
import com.pos.cashiersp.model.dto.StoreStockGetV2Dto
import com.pos.cashiersp.model.dto.TenantGetMembersDto
import com.pos.cashiersp.model.dto.TransactionResponse
import com.pos.cashiersp.presentation.util.LoginRequestBody
import com.pos.cashiersp.presentation.util.NewTenantRequestBody
import com.pos.cashiersp.presentation.util.SignUpWithEmailAndPasswordRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CashierApi {
    // Tenant
    @GET("tenants/members/{tenantId}")
    suspend fun getTenantMembers(@Path("tenantId") tenantId: String)
            : Response<HTTPStatus.SuccessResponse<TenantGetMembersDto>>

    @GET("tenants/{tenantId}")
    suspend fun getTenantWithUser(@Path("tenantId") tenantId: String):
            Response<HTTPStatus.SuccessResponse<GetTenantWithUserDto>>

    @POST("tenants/new")
    suspend fun newTenant(@Body newTenantRequest: NewTenantRequestBody)
            : Response<ResponseBody>

    // User
    @POST("users/sign_in")
    suspend fun loginRequest(@Body loginRequest: LoginRequestBody)
            : Response<HTTPStatus.SuccessResponse<LoginResponseDto>>

    @POST("users/sign_up")
    suspend fun signUpWithEmailAndPassword(@Body signUpWithEmailAndPassword: SignUpWithEmailAndPasswordRequestBody)
            : Response<HTTPStatus.SuccessResponse<SignUpResponseDto>>

    // Store
    @GET("stores/{tenantId}")
    suspend fun storeGetAll(
        @Path("tenantId") tenantId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("include_non_active") includeNonActive: Boolean
    ): Response<HTTPStatus.SuccessResponse<GetAllStoreDto>>

    // Store Stock
    @GET("store_stocks/{tenantId}")
    suspend fun storeStockGetV2(
        @Path("tenantId") tenantId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("store_id") storeId: Int,
        @Query("name_query") nameQuery: String,
    ): Response<HTTPStatus.SuccessResponse<StoreStockGetV2Dto>>

    @GET("store_stocks/load_cashier_data/{tenantId}")
    suspend fun loadCashierData(
        @Path("tenantId") tenantId: Int,
        @Query("store_id") storeId: Int,
    ): Response<HTTPStatus.SuccessResponse<CashierDataDto>>

    // Order Items
    @POST("order_items/transactions/{tenantId}")
    suspend fun transactions(
        @Body createTransactionParams: CreateTransactionParams,
        @Path("tenantId") tenantId: Int,
    ): Response<HTTPStatus.SuccessResponse<TransactionResponse>>
}