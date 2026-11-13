package com.amarchaud.shared.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.shared.data.Language
import com.amarchaud.shared.data.ResumeModel
import com.amarchaud.shared.data.ResumeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ResumeUiState {
    data object Loading : ResumeUiState
    data class Success(val resumes: Map<Language, ResumeModel>) : ResumeUiState
    data class Error(val message: String) : ResumeUiState
}

class ResumeViewModel(private val repository: ResumeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ResumeUiState>(ResumeUiState.Loading)
    val uiState: StateFlow<ResumeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = runCatching { repository.loadResumes() }.fold(
                onSuccess = { ResumeUiState.Success(it) },
                onFailure = { ResumeUiState.Error(it.message ?: it.toString()) },
            )
        }
    }
}
