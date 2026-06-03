package com.callrecord.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.callrecord.app.domain.model.CallType

@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneNumber: String,
    val contactName: String,
    val callType: CallType,
    val startedAt: Long,
    val durationMs: Long,
    val filePath: String,
    val audioSource: Int
)
