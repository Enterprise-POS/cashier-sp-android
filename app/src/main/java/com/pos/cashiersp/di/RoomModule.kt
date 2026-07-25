package com.pos.cashiersp.di

import android.app.Application
import androidx.room.Room
import com.pos.cashiersp.model.room_entity.CashierDB
import com.pos.cashiersp.model.room_entity.DatabaseCacheMetadataDao
import com.pos.cashiersp.repository.DatabaseCacheMetadataImpl
import com.pos.cashiersp.repository.DatabaseCacheMetadataRepository
import com.pos.cashiersp.use_case.DatabaseCacheMetadataUseCase
import com.pos.cashiersp.use_case.GetMetadata
import com.pos.cashiersp.use_case.WriteAndReturnMetadata
import com.pos.cashiersp.use_case.WriteMetadata
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
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    @Singleton
    fun provideDatabaseCacheMetadataRepository(db: CashierDB): DatabaseCacheMetadataRepository {
        return DatabaseCacheMetadataImpl(db.databaseCacheMetadataDao)
    }

    @Provides
    @Singleton
    fun provideDatabaseCacheMetadataUseCase(repository: DatabaseCacheMetadataRepository): DatabaseCacheMetadataUseCase {
        return DatabaseCacheMetadataUseCase(
            writeMetadata = WriteMetadata(repository),
            getMetadata = GetMetadata(repository),
            writeAndReturnMetadata = WriteAndReturnMetadata(repository),
        )
    }
}