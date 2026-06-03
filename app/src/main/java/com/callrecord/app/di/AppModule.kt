package com.callrecord.app.di

import android.content.Context
import com.callrecord.app.data.repository.RecordingRepositoryImpl
import com.callrecord.app.domain.repository.RecordingRepository
import com.callrecord.app.utils.CallStateStore
import com.callrecord.app.utils.ContactResolver
import com.callrecord.app.utils.FileNameFormatter
import com.callrecord.app.utils.SettingsStore
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
    fun provideSettingsStore(@ApplicationContext context: Context): SettingsStore {
        return SettingsStore(context)
    }

    @Provides
    @Singleton
    fun provideCallStateStore(@ApplicationContext context: Context): CallStateStore {
        return CallStateStore(context)
    }

    @Provides
    @Singleton
    fun provideContactResolver(@ApplicationContext context: Context): ContactResolver {
        return ContactResolver(context)
    }

    @Provides
    @Singleton
    fun provideFileNameFormatter(): FileNameFormatter {
        return FileNameFormatter()
    }

    @Provides
    @Singleton
    fun provideRecordingRepository(impl: RecordingRepositoryImpl): RecordingRepository {
        return impl
    }
}
