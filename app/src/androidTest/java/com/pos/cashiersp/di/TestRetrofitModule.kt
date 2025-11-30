package com.pos.cashiersp.di

import com.pos.cashiersp.common.Constants
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.repository.TenantRepository
import com.pos.cashiersp.repository.TenantRepositoryImpl
import com.pos.cashiersp.use_case.GetTenantMembers
import com.pos.cashiersp.use_case.TenantUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestRetrofitModule {
    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    @Provides
    @Singleton
    fun provideMockWebServer(): MockWebServer = MockWebServer()

    @Provides
    @Singleton
    fun provideCashierAPI(okHttpClient: OkHttpClient, mockWebServer: MockWebServer): CashierApi {
        return Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(CashierApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTenantRepository(api: CashierApi): TenantRepository {
        return TenantRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideTenantUseCase(repository: TenantRepository): TenantUseCase {
        return TenantUseCase(
            getTenantMembers = GetTenantMembers(repository)
        )
    }
}