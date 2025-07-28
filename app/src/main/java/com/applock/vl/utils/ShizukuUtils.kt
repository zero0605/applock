package com.applock.vl.utils

import rikka.shizuku.Shizuku

object ShizukuUtils {
    
    fun isShizukuRunning(): Boolean {
        return try {
            Shizuku.pingBinder()
            true
        } catch (e: Exception) {
            android.util.Log.e("ShizukuUtils", "pingBinder failed: ${e.message}")
            false
        }
    }
    
    fun hasShizukuPermission(): Boolean {
        return try {
            val result = Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
            android.util.Log.d("ShizukuUtils", "permission check: $result")
            result
        } catch (e: Exception) {
            android.util.Log.e("ShizukuUtils", "permission check failed: ${e.message}")
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
    
    fun getShizukuStatus(): String {
        return try {
            when {
                !isShizukuRunning() -> "shizuku chua chay"
                !hasShizukuPermission() -> "chua cap quyen shizuku"
                else -> "shizuku ok"
            }
        } catch (e: Exception) {
            "loi: ${e.message}"
        }
    }
}
