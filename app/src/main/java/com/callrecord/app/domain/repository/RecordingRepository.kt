package com.callrecord.app.domain.repository

import com.callrecord.app.domain.model.CallRecording
import com.callrecord.app.domain.model.CallType
import kotlinx.coroutines.flow.Flow

interface RecordingRepository {
    fun observeRecordings(query: String, callType: CallType?): Flow<List<CallRecording>>
    suspend fun getRecording(id: Long): CallRecording?
    suspend fun insertRecording(recording: CallRecording): Long
    suspend fun deleteRecording(recording: CallRecording)
}
