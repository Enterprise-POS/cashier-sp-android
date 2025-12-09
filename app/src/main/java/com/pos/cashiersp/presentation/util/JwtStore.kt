package com.pos.cashiersp.presentation.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pos.cashiersp.BuildConfig
import com.pos.cashiersp.model.dto.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.jwtDataStore by preferencesDataStore(name = BuildConfig.DS_NAME)

data class UserPayload(
    val token: String,
    val name: String,
    val sub: Int,
    //val exp: Instant?
)

class JwtStore(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val NAME = stringPreferencesKey("username")
        private val SUB = intPreferencesKey("sub")
        // private val CREATED_AT = stringPreferencesKey()

        // This will not explicitly return from JSON, but will read from requested cookie
        //private val EXP = stringPreferencesKey("exp")
    }

    // Save token
    suspend fun saveToken(token: String, user: User) {
        context.jwtDataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[NAME] = user.name
            prefs[SUB] = user.id
            /*
            prefs[EXP] = Date.from(
                Instant.now().plus(30, ChronoUnit.DAYS)
            ).toString()
            * */
        }
    }

    // Get / Read token
    fun getPayload(): Flow<UserPayload?> =
        context.jwtDataStore.data.map { prefs ->
            val isTokenAvailable = prefs[TOKEN_KEY]?.isEmpty() == true
            if (isTokenAvailable) return@map null

            return@map UserPayload(
                token = prefs[TOKEN_KEY] ?: "",
                name = prefs[NAME] ?: "",
                sub = prefs[SUB] ?: 0,
                //exp = prefs[EXP]?.let { Instant.parse(it) },
            )
        }

    suspend fun clearToken() {
        context.jwtDataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(NAME)
            prefs.remove(SUB)
        }
    }
}