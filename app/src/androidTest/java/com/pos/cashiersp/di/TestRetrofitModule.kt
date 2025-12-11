package com.pos.cashiersp.di

import com.pos.cashiersp.common.Constants
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.presentation.util.JwtStore
import com.pos.cashiersp.presentation.util.MyCookieImpl
import com.pos.cashiersp.repository.TenantRepository
import com.pos.cashiersp.repository.TenantRepositoryImpl
import com.pos.cashiersp.repository.UserRepository
import com.pos.cashiersp.repository.UserRepositoryImpl
import com.pos.cashiersp.use_case.GetTenantMembers
import com.pos.cashiersp.use_case.IsLoggedIn
import com.pos.cashiersp.use_case.LoginRequest
import com.pos.cashiersp.use_case.Logout
import com.pos.cashiersp.use_case.SignUpWithEmailAndPasswordRequest
import com.pos.cashiersp.use_case.TenantUseCase
import com.pos.cashiersp.use_case.UserUseCase
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
    @Provides
    @Singleton
    fun provideCookieJar(): MyCookieImpl {
        return MyCookieImpl()
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(cookieJar: MyCookieImpl): OkHttpClient =
        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
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
    fun provideTenantUseCase(repository: TenantRepository, jwtStore: JwtStore): TenantUseCase {
        return TenantUseCase(
            getTenantMembers = GetTenantMembers(repository),
            getTenantWithUser = GetTenantWithUser(repository, jwtStore)
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: CashierApi): UserRepository {
        return UserRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideUserUseCases(repository: UserRepository, jwtStore: JwtStore, myCookieImpl: MyCookieImpl): UserUseCase {
        return UserUseCase(
            loginRequest = LoginRequest(repository, jwtStore, myCookieImpl),
            signUpWithEmailAndPassword = SignUpWithEmailAndPasswordRequest(repository, jwtStore, myCookieImpl),
            isLoggedIn = IsLoggedIn(jwtStore, myCookieImpl),
            logout = Logout(jwtStore, myCookieImpl)
        )
    }
}