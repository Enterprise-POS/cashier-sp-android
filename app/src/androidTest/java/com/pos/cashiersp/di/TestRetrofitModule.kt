package com.pos.cashiersp.di

import com.pos.cashiersp.common.Constants
import com.pos.cashiersp.model.CashierApi
import com.pos.cashiersp.model.room_entity.CashierDB
import com.pos.cashiersp.presentation.util.JwtStore
import com.pos.cashiersp.presentation.util.MyCookieImpl
import com.pos.cashiersp.repository.OrderItemRepository
import com.pos.cashiersp.repository.OrderItemRepositoryImpl
import com.pos.cashiersp.repository.StoreRepository
import com.pos.cashiersp.repository.StoreRepositoryImpl
import com.pos.cashiersp.repository.StoreStockRepository
import com.pos.cashiersp.repository.StoreStockRepositoryImpl
import com.pos.cashiersp.repository.TenantRepository
import com.pos.cashiersp.repository.TenantRepositoryImpl
import com.pos.cashiersp.repository.UserRepository
import com.pos.cashiersp.repository.UserRepositoryImpl
import com.pos.cashiersp.use_case.FindTransactionsById
import com.pos.cashiersp.use_case.GetAllStore
import com.pos.cashiersp.use_case.GetTenantMembers
import com.pos.cashiersp.use_case.GetTenantWithUser
import com.pos.cashiersp.use_case.IsLoggedIn
import com.pos.cashiersp.use_case.LoadCashierData
import com.pos.cashiersp.use_case.LoginRequest
import com.pos.cashiersp.use_case.Logout
import com.pos.cashiersp.use_case.NewTenant
import com.pos.cashiersp.use_case.OrderItemUseCase
import com.pos.cashiersp.use_case.SearchTransactions
import com.pos.cashiersp.use_case.SignUpWithEmailAndPasswordRequest
import com.pos.cashiersp.use_case.StoreStockGetV2
import com.pos.cashiersp.use_case.StoreStockUseCase
import com.pos.cashiersp.use_case.StoreUseCase
import com.pos.cashiersp.use_case.TenantUseCase
import com.pos.cashiersp.use_case.Transactions
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


    @Provides
    @Singleton
    fun provideMockWebServer(): MockWebServer = MockWebServer()

    @Provides
    @Singleton
    fun provideOkHttpClient(cookieJar: MyCookieImpl): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideCashierAPI(client: OkHttpClient, mockWebServer: MockWebServer): CashierApi {
        return Retrofit.Builder()
            // Since it's a mock server, don't need real server url
            .baseUrl(mockWebServer.url("/"))
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
    fun provideTenantUseCases(repository: TenantRepository, jwtStore: JwtStore): TenantUseCase {
        return TenantUseCase(
            getTenantMembers = GetTenantMembers(repository),
            getTenantWithUser = GetTenantWithUser(repository, jwtStore),
            newTenant = NewTenant(repository, jwtStore)
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

    @Provides
    @Singleton
    fun provideStoreRepository(api: CashierApi): StoreRepository {
        return StoreRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideStoreUseCase(repository: StoreRepository): StoreUseCase {
        return StoreUseCase(
            getAll = GetAllStore(repository)
        )
    }

    @Provides
    @Singleton
    fun provideStoreStockRepository(api: CashierApi, db: CashierDB): StoreStockRepository {
        return StoreStockRepositoryImpl(api, db.cashierItemDao)
    }

    @Provides
    @Singleton
    fun provideStoreStockUseCase(repository: StoreStockRepository): StoreStockUseCase {
        return StoreStockUseCase(
            getV2 = StoreStockGetV2(repository),
            loadCashierData = LoadCashierData(repository),
        )
    }

    @Provides
    @Singleton
    fun provideOrderItemRepository(api: CashierApi): OrderItemRepository {
        return OrderItemRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideOrderItemUseCase(repository: OrderItemRepository): OrderItemUseCase {
        return OrderItemUseCase(
            transaction = Transactions(repository),
            searchTransactions = SearchTransactions(repository),
            findTransactionsById = FindTransactionsById(repository)
        )
    }
}