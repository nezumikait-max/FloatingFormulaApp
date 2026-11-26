package com.example.myapplication.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        val Y_OFFSET = floatPreferencesKey("y_offset")
    }

    val yOffsetFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[Y_OFFSET] ?: 0.5f // Default to 0.5 (center)
    }

    suspend fun saveYOffset(offset: Float) {
        context.dataStore.edit { preferences ->
            preferences[Y_OFFSET] = offset
        }
    }
}
