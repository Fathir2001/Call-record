package com.callrecord.app.data.model

import androidx.room.TypeConverter
import com.callrecord.app.domain.model.CallType

class Converters {

    @TypeConverter
    fun fromCallType(type: CallType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toCallType(value: String?): CallType {
        return if (value.isNullOrBlank()) {
            CallType.UNKNOWN
        } else {
            CallType.valueOf(value)
        }
    }
}
