package com.callrecord.app.data.repository

import com.callrecord.app.data.db.RecordingDao
import com.callrecord.app.data.model.RecordingEntity
import com.callrecord.app.domain.model.CallRecording
import com.callrecord.app.domain.model.CallType
import com.callrecord.app.domain.repository.RecordingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordingRepositoryImpl @Inject constructor(
    private val recordingDao: RecordingDao
) : RecordingRepository {

    override fun observeRecordings(query: String, callType: CallType?): Flow<List<CallRecording>> {
        return recordingDao.observeRecordings(query, callType)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getRecording(id: Long): CallRecording? {
        return recordingDao.getById(id)?.toDomain()
    }

    override suspend fun insertRecording(recording: CallRecording): Long {
        return recordingDao.insert(recording.toEntity())
    }

    override suspend fun deleteRecording(recording: CallRecording) {
        recordingDao.delete(recording.toEntity())
    }
}

private fun RecordingEntity.toDomain(): CallRecording {
    return CallRecording(
        id = id,
        phoneNumber = phoneNumber,
        contactName = contactName,
        callType = callType,
        startedAt = startedAt,
        durationMs = durationMs,
        filePath = filePath,
        audioSource = audioSource
    )
}

private fun CallRecording.toEntity(): RecordingEntity {
    return RecordingEntity(
        id = id,
        phoneNumber = phoneNumber,
        contactName = contactName,
        callType = callType,
        startedAt = startedAt,
        durationMs = durationMs,
        filePath = filePath,
        audioSource = audioSource
    )
}
