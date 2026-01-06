package com.pos.cashiersp.di

import android.content.Context
import com.pos.cashiersp.presentation.util.JwtStore
import com.pos.cashiersp.presentation.util.SelectedStoreDS
import com.pos.cashiersp.presentation.util.SelectedTenantDS
import com.pos.cashiersp.use_case.DataStoreUseCase
import com.pos.cashiersp.use_case.GetCurrentSelectedStore
import com.pos.cashiersp.use_case.GetCurrentSelectedTenant
import com.pos.cashiersp.use_case.SaveSelectedStore
import com.pos.cashiersp.use_case.SaveSelectedTenant
import com.pos.cashiersp.use_case.UnselectStore
import com.pos.cashiersp.use_case.UnselectTenant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideJwtStore(@ApplicationContext context: Context): JwtStore {
        return JwtStore(context)
    }

    @Provides
    @Singleton
    fun provideSelectedStoreDS(@ApplicationContext context: Context): SelectedStoreDS {
        return SelectedStoreDS(context)
    }

    @Provides
    @Singleton
    fun provideSelectedTenantDS(@ApplicationContext context: Context): SelectedTenantDS {
        return SelectedTenantDS(context)
    }

    @Provides
    @Singleton
    fun provideStoreUseCase(tenantDS: SelectedTenantDS, storeDS: SelectedStoreDS): DataStoreUseCase {
        return DataStoreUseCase(
            saveSelectedTenant = SaveSelectedTenant(tenantDS),
            getCurrentTenant = GetCurrentSelectedTenant(tenantDS),
            unselectTenant = UnselectTenant(tenantDS),

            saveSelectedStore = SaveSelectedStore(storeDS),
            getCurrentStore = GetCurrentSelectedStore(storeDS),
            unselectStore = UnselectStore(storeDS),
        )
    }
}

