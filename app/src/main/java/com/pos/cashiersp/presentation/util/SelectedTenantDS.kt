package com.pos.cashiersp.presentation.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pos.cashiersp.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.selectedTenantDS by preferencesDataStore(name = BuildConfig.DS_SELECTED_TENANT)

data class SelectedTenantDSPayload(
    val id: Int,
    val name: String
)

class SelectedTenantDS(private val context: Context) {
    companion object {
        private val ID = intPreferencesKey("id")
        private val NAME = stringPreferencesKey("name")
    }

    // Save token
    suspend fun saveSelectedTenant(id: Int, name: String) {
        context.selectedTenantDS.edit { prefs ->
            prefs[ID] = id
            prefs[NAME] = name
        }
    }

    // Get / Read token
    fun getPayload(): Flow<SelectedTenantDSPayload?> =
        context.selectedTenantDS.data.map { prefs ->
            val id = prefs[ID]
            val name = prefs[NAME]

            if (name.isNullOrEmpty() || id == null) {
                return@map null
            }

            return@map SelectedTenantDSPayload(id, name)
        }

    suspend fun unselectAnything() {
        context.selectedTenantDS.edit { prefs ->
            prefs.remove(ID)
            prefs.remove(NAME)
        }
    }
}