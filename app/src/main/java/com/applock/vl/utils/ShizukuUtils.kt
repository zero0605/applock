package com.applock.vl.utils

import android.util.Log
import rikka.shizuku.Shizuku

object ShizukuUtils {

    private const val TAG = "ShizukuUtils"

    fun isShizukuAvailable(): Boolean {
        return try {
            // check if shizuku is available
            Shizuku.getVersion() >= 10
        } catch (e: Exception) {
            Log.e(TAG, "shizuku not available: ${e.message}")
            false
        }
    }

    fun isShizukuRunning(): Boolean {
        return try {
            if (!isShizukuAvailable()) return false
            Shizuku.pingBinder()
            Log.d(TAG, "shizuku ping successful")
            true
        } catch (e: Exception) {
            Log.e(TAG, "shizuku ping failed: ${e.message}")
            false
        }
    }

    fun hasShizukuPermission(): Boolean {
        return try {
            if (!isShizukuRunning()) return false
            val permission = Shizuku.checkSelfPermission()
            val granted = permission == android.content.pm.PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "permission status: $permission, granted: $granted")
            granted
        } catch (e: Exception) {
            Log.e(TAG, "permission check failed: ${e.message}")
            false
        }
    }

    fun requestShizukuPermission() {
        try {
            if (isShizukuRunning()) {
                Shizuku.requestPermission(1001)
                Log.d(TAG, "permission requested")
            }
        } catch (e: Exception) {
            Log.e(TAG, "permission request failed: ${e.message}")
        }
    }

    fun getShizukuStatus(): String {
        return try {
            when {
                !isShizukuAvailable() -> "shizuku khong co san"
                !isShizukuRunning() -> "shizuku chua chay"
                !hasShizukuPermission() -> "chua cap quyen shizuku"
                else -> "shizuku ok"
            }
        } catch (e: Exception) {
            "loi: ${e.message}"
        }
    }

    fun getDetailedStatus(): String {
        val sb = StringBuilder()
        try {
            sb.append("available: ${isShizukuAvailable()}\n")
            sb.append("running: ${isShizukuRunning()}\n")
            sb.append("permission: ${hasShizukuPermission()}\n")
            if (isShizukuAvailable()) {
                sb.append("version: ${Shizuku.getVersion()}")
            }
        } catch (e: Exception) {
            sb.append("error: ${e.message}")
        }
        return sb.toString()
    }
}
