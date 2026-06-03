package com.callrecord.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.callrecord.app.data.model.Converters
import com.callrecord.app.data.model.RecordingEntity

@Database(entities = [RecordingEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordingDao(): RecordingDao
}
