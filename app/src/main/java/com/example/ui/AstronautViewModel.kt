package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AstronautProfileState
import com.example.data.AstronautProgressRepository
import com.example.data.MissionId
import com.example.data.MissionRunResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AstronautViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AstronautProgressRepository(application.applicationContext)

    val profileState: StateFlow<AstronautProfileState> = repository.profileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AstronautProfileState()
        )

    private val _lastResult = MutableStateFlow<MissionRunResult?>(null)
    val lastResult: StateFlow<MissionRunResult?> = _lastResult.asStateFlow()

    fun updateAstronautName(newName: String) {
        viewModelScope.launch {
            repository.saveAstronautName(newName)
        }
    }

    fun recordMissionCompletion(result: MissionRunResult) {
        _lastResult.value = result
        viewModelScope.launch {
            repository.saveMissionResult(
                missionId = result.missionId,
                score = result.score,
                starsEarned = result.starsEarned
            )
        }
    }

    fun clearLastResult() {
        _lastResult.value = null
    }

    fun resetTrainingProgress() {
        viewModelScope.launch {
            repository.resetProgress()
        }
    }
}
