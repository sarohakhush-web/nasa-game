package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.astronautDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "junior_astronaut_progress"
)

class AstronautProgressRepository(private val context: Context) {

    private object Keys {
        val ASTRONAUT_NAME = stringPreferencesKey("astronaut_name")

        fun starsKey(id: MissionId) = intPreferencesKey("stars_${id.name.lowercase()}")
        fun scoreKey(id: MissionId) = intPreferencesKey("score_${id.name.lowercase()}")
    }

    val profileFlow: Flow<AstronautProfileState> = context.astronautDataStore.data.map { prefs ->
        val name = prefs[Keys.ASTRONAUT_NAME]?.takeIf { it.isNotBlank() } ?: "Cadet Nova"
        val stars = MissionId.entries.associateWith { id ->
            (prefs[Keys.starsKey(id)] ?: 0).coerceIn(0, 3)
        }
        val scores = MissionId.entries.associateWith { id ->
            (prefs[Keys.scoreKey(id)] ?: 0).coerceAtLeast(0)
        }
        AstronautProfileState(
            astronautName = name,
            missionStars = stars,
            missionBestScores = scores,
            isLoaded = true
        )
    }

    suspend fun saveAstronautName(name: String) {
        val cleaned = name.trim().take(24)
        context.astronautDataStore.edit { prefs ->
            prefs[Keys.ASTRONAUT_NAME] = cleaned.ifEmpty { "Cadet Nova" }
        }
    }

    suspend fun saveMissionResult(missionId: MissionId, score: Int, starsEarned: Int) {
        val clampedStars = starsEarned.coerceIn(1, 3)
        val clampedScore = score.coerceAtLeast(0)
        context.astronautDataStore.edit { prefs ->
            val currentStars = prefs[Keys.starsKey(missionId)] ?: 0
            val currentScore = prefs[Keys.scoreKey(missionId)] ?: 0
            if (clampedStars > currentStars) {
                prefs[Keys.starsKey(missionId)] = clampedStars
            }
            if (clampedScore > currentScore) {
                prefs[Keys.scoreKey(missionId)] = clampedScore
            }
        }
    }

    suspend fun resetProgress() {
        context.astronautDataStore.edit { prefs ->
            val preservedName = prefs[Keys.ASTRONAUT_NAME] ?: "Cadet Nova"
            prefs.clear()
            prefs[Keys.ASTRONAUT_NAME] = preservedName
        }
    }
}
