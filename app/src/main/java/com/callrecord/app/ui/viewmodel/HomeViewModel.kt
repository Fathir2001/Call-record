package com.callrecord.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callrecord.app.domain.model.CallRecording
import com.callrecord.app.domain.model.CallType
import com.callrecord.app.domain.repository.RecordingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecordingRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(CallTypeFilter.ALL)

    private val recordings = combine(query, filter) { q, f -> q to f }
        .flatMapLatest { (q, f) ->
            val formatted = if (q.isBlank()) "%" else "%${q.trim()}%"
            repository.observeRecordings(formatted, f.toCallType())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<HomeUiState> = combine(query, filter, recordings) { q, f, list ->
        HomeUiState(
            query = q,
            filter = f,
            recordings = list
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun updateQuery(value: String) {
        query.value = value
    }

    fun updateFilter(value: CallTypeFilter) {
        filter.value = value
    }
}

data class HomeUiState(
    val query: String = "",
    val filter: CallTypeFilter = CallTypeFilter.ALL,
    val recordings: List<CallRecording> = emptyList()
)

enum class CallTypeFilter(val label: String) {
    ALL("All"),
    INCOMING("Incoming"),
    OUTGOING("Outgoing");

    fun toCallType(): CallType? {
        return when (this) {
            ALL -> null
            INCOMING -> CallType.INCOMING
            OUTGOING -> CallType.OUTGOING
        }
    }
}
