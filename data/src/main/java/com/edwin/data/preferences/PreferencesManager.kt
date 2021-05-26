package com.edwin.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.edwin.domain.model.SortOrder
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

enum class AppTheme {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun valuesAsString(): Array<String> = values().map { it.name }.toTypedArray()
    }
}

class PreferencesManager(context: Context) {
    private val dataStore = context.dataStore

    val sortOrder = dataStore.data
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_FIRST_NAME.name
            )
            sortOrder
        }

    val appTheme = dataStore.data
        .map { preferences ->
            val appTheme = AppTheme.valueOf(
                preferences[PreferencesKeys.APP_THEME] ?: AppTheme.SYSTEM.name
            )
            appTheme
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateAppTheme(appTheme: AppTheme) {
        dataStore.edit { theme ->
            theme[PreferencesKeys.APP_THEME] = appTheme.name
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val APP_THEME = stringPreferencesKey("app_theme")
    }
}