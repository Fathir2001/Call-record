package com.callrecord.app.domain.model

enum class CallType(val label: String) {
    INCOMING("Incoming"),
    OUTGOING("Outgoing"),
    UNKNOWN("Unknown")
}
