package com.example.twinmind.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinmind.data.Recording
import com.example.twinmind.data.RecordingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    private val repository: RecordingRepository
) : ViewModel() {

    // Expose a stream of recordings for the Dashboard UI.
    val recordings: StateFlow<List<Recording>> = repository.getAllRecordings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Retrieve a single recording by ID for the Detail screen.
    fun getRecording(id: Int): StateFlow<Recording?> {
        return repository.getRecordingById(id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    // Triggers the mock API process to generate summary/transcript.
    fun generateContent(id: Int) {
        viewModelScope.launch {
            repository.generateFakeTranscript(id)
            repository.generateFakeSummary(id)
        }
    }
}