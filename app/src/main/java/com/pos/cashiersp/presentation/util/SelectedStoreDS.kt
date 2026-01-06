package com.pos.cashiersp.presentation.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pos.cashiersp.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.selectedStoreDS by preferencesDataStore(name = BuildConfig.DS_SELECTED_STORE)

data class SelectedStoreDSPayload(
    val id: Int,
    val name: String,
)

class SelectedStoreDS(private val context: Context) {
    companion object {
        private val ID = intPreferencesKey("id")
        private val NAME = stringPreferencesKey("name")

        /*
        *
            private val STORE_ID = intPreferencesKey("store_id")
            private val STORE_IS_ACTIVE = booleanPreferencesKey("store_is_active")
            private val STORE_NAME = stringPreferencesKey("store_name")
            private val STORE_TENANT_ID = intPreferencesKey("store_tenant_id")
            private val STORE_CREATED_AT = stringPreferencesKey("store_created_at")
        * */
    }

    suspend fun saveSelectedStore(id: Int, name: String) {
        context.selectedStoreDS.edit { prefs ->
            prefs[ID] = id
            prefs[NAME] = name
        }
    }

    // Get / Read token
    fun getPayload(): Flow<SelectedStoreDSPayload?> =
        context.selectedStoreDS.data.map { prefs ->
            val id = prefs[ID]
            val name = prefs[NAME]

            if (name.isNullOrEmpty() || id == null) {
                return@map null
            }

            return@map SelectedStoreDSPayload(id, name)
        }

    suspend fun unselectAnything() {
        context.selectedStoreDS.edit { prefs ->
            prefs.remove(ID)
        }
    }
}
