package com.student.githubdemo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "github_demo_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    
    companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val USER_LOGIN_KEY = stringPreferencesKey("user_login")
    }
    
    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }
    
    val userLogin: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_LOGIN_KEY]
    }
    
    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }
    
    suspend fun saveUserLogin(login: String) {
        dataStore.edit { preferences ->
            preferences[USER_LOGIN_KEY] = login
        }
    }
    
    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(USER_LOGIN_KEY)
        }
    }
}
