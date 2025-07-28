package com.applock.vl.utils

import android.content.Context
import android.content.SharedPreferences

object PrefsUtils {
    
    private const val PREFS_NAME = "applock_prefs"
    private const val KEY_LOCKED_APPS = "locked_apps"
    private const val KEY_PIN = "pin"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun getLockedApps(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_LOCKED_APPS, emptySet()) ?: emptySet()
    }
    
    fun setLockedApps(context: Context, apps: Set<String>) {
        getPrefs(context).edit()
            .putStringSet(KEY_LOCKED_APPS, apps)
            .apply()
    }
    
    fun addLockedApp(context: Context, packageName: String) {
        val current = getLockedApps(context).toMutableSet()
        current.add(packageName)
        setLockedApps(context, current)
    }
    
    fun removeLockedApp(context: Context, packageName: String) {
        val current = getLockedApps(context).toMutableSet()
        current.remove(packageName)
        setLockedApps(context, current)
    }
    
    fun isAppLocked(context: Context, packageName: String): Boolean {
        return getLockedApps(context).contains(packageName)
    }
    
    fun getPin(context: Context): String {
        return getPrefs(context).getString(KEY_PIN, "1234") ?: "1234"
    }
    
    fun setPin(context: Context, pin: String) {
        getPrefs(context).edit()
            .putString(KEY_PIN, pin)
            .apply()
    }
}
