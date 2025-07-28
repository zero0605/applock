package com.applock.vl.utils

import rikka.shizuku.Shizuku

object ShizukuUtils {
    
    fun isShizukuRunning(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }
    
    fun hasShizukuPermission(): Boolean {
        return try {
            Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }
    
    fun requestShizukuPermission() {
        try {
            Shizuku.requestPermission(1001)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
