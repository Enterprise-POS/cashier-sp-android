package com.pos.cashiersp.presentation.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pos.cashiersp.BuildConfig
import kotlinx.coroutines.flow.map
import java.util.Date

val Context.jwtDataStore by preferencesDataStore(name = BuildConfig.DS_NAME)

data class UserPayload(
    val token: String,
    val username: String,
    val sub: Int,
    val exp: Date
)

object JwtStore {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val USERNAME = stringPreferencesKey("username")
    private val SUB = stringPreferencesKey("sub")
    // private val CREATED_AT = stringPreferencesKey()

    // This will not explicitly return from JSON, but will read from requested cookie
    private val EXP = stringPreferencesKey("exp")

    // Save token
    suspend fun saveToken(context: Context, token: String) {
        context.jwtDataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    // Get / Read token
    fun getToken(context: Context) =
        context.jwtDataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }

    suspend fun clearToken(context: Context) {
        context.jwtDataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}