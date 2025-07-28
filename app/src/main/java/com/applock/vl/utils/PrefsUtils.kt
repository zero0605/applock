package com.applock.vl.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "applock_prefs")

object PrefsUtils {
    
    private val PIN_KEY = stringPreferencesKey("pin")
    private val LOCKED_APPS_KEY = stringSetPreferencesKey("locked_apps")
    
    fun getPin(context: Context): String {
        return runBlocking {
            context.dataStore.data.map { prefs ->
                prefs[PIN_KEY] ?: "1234"
            }.first()
        }
    }
    
    suspend fun setPin(context: Context, pin: String) {
        context.dataStore.edit { prefs ->
            prefs[PIN_KEY] = pin
        }
    }
    
    fun getLockedApps(context: Context): Set<String> {
        return runBlocking {
            context.dataStore.data.map { prefs ->
                prefs[LOCKED_APPS_KEY] ?: emptySet()
            }.first()
        }
    }
    
    suspend fun setLockedApps(context: Context, apps: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[LOCKED_APPS_KEY] = apps
        }
    }
}
