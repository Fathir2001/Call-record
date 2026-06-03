package com.callrecord.app.di

import android.content.Context
import androidx.room.Room
import com.callrecord.app.data.db.AppDatabase
import com.callrecord.app.data.db.RecordingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "call_record.db")
            .build()
    }

    @Provides
    fun provideRecordingDao(database: AppDatabase): RecordingDao {
        return database.recordingDao()
    }
}
