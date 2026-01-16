package com.pos.cashiersp.di

import android.app.Application
import androidx.room.Room
import com.pos.cashiersp.model.room_entity.CashierDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideCashierDB(app: Application): CashierDB {
        return Room.databaseBuilder(
            app,
            CashierDB::class.java,
            CashierDB.DATABASE_NAME
        ).build()
    }
}