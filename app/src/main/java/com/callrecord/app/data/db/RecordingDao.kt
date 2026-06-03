package com.callrecord.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.callrecord.app.data.model.RecordingEntity
import com.callrecord.app.domain.model.CallType
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {

    @Query(
        "SELECT * FROM recordings " +
            "WHERE (contactName LIKE :query OR phoneNumber LIKE :query) " +
            "AND (:callType IS NULL OR callType = :callType) " +
            "ORDER BY startedAt DESC"
    )
    fun observeRecordings(query: String, callType: CallType?): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): RecordingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecordingEntity): Long

    @Delete
    suspend fun delete(entity: RecordingEntity)
}
