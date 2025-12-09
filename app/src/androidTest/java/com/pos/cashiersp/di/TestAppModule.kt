package com.pos.cashiersp.di

import android.content.Context
import com.pos.cashiersp.presentation.util.JwtStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {
    @Provides
    @Singleton
    fun provideJwtStore(@ApplicationContext context: Context): JwtStore {
        return JwtStore(context)
    }
}