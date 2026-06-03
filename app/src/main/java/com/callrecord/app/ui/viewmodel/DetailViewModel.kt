package com.callrecord.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callrecord.app.domain.model.CallRecording
import com.callrecord.app.domain.repository.RecordingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: RecordingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recordingId = savedStateHandle.get<Long>("recordingId") ?: 0L
    private val _recording = MutableStateFlow<CallRecording?>(null)
    val recording: StateFlow<CallRecording?> = _recording

    init {
        viewModelScope.launch {
            _recording.value = repository.getRecording(recordingId)
        }
    }

    fun deleteRecording() {
        val current = _recording.value ?: return
        viewModelScope.launch {
            repository.deleteRecording(current)
            File(current.filePath).delete()
            _recording.value = null
        }
    }
}
