package com.pos.cashiersp.di

import com.pos.cashiersp.common.Constants
import com.pos.cashiersp.common.InMemoryCookieJar
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.presentation.util.JwtStore
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
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @Singleton
    fun provideCookieJar(): CookieJar {
        return InMemoryCookieJar()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cookieJar: CookieJar): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()
    }

    @Provides
    @Singleton
    fun provideCashierAPI(client: OkHttpClient): CashierApi {
        return Retrofit.Builder()
            .baseUrl("${Constants.BASE_BE_URL}/${Constants.BE_API_VERSION}/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
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
    fun provideTenantUseCases(repository: TenantRepository): TenantUseCase {
        return TenantUseCase(
            getTenantMembers = GetTenantMembers(repository),
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: CashierApi): UserRepository {
        return UserRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideUserUseCases(repository: UserRepository, jwtStore: JwtStore): UserUseCase {
        return UserUseCase(
            loginRequest = LoginRequest(repository, jwtStore),
            signUpWithEmailAndPassword = SignUpWithEmailAndPasswordRequest(repository, jwtStore),
            isLoggedIn = IsLoggedIn(jwtStore),
            logout = Logout(jwtStore)
        )
    }
}