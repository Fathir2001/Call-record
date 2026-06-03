package com.callrecord.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.callrecord.app.utils.SettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(autoRecordEnabled = settingsStore.isAutoRecordEnabled())
    )
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun setAutoRecordEnabled(enabled: Boolean) {
        settingsStore.setAutoRecordEnabled(enabled)
        _uiState.value = _uiState.value.copy(autoRecordEnabled = enabled)
    }
}

data class SettingsUiState(
    val autoRecordEnabled: Boolean
)
