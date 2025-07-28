package com.applock.vl.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.applock.vl.data.AppInfo

object AppUtils {
    
    fun getInstalledApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        return try {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { appInfo ->
                    // filter out system apps and current app
                    appInfo.packageName != context.packageName &&
                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                    pm.getLaunchIntentForPackage(appInfo.packageName) != null
                }
                .map { appInfo ->
                    AppInfo(
                        packageName = appInfo.packageName,
                        name = pm.getApplicationLabel(appInfo).toString(),
                        icon = try { pm.getApplicationIcon(appInfo) } catch (e: Exception) { null }
                    )
                }
                .sortedBy { it.name }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getPopularApps(): List<AppInfo> {
        return listOf(
            AppInfo("com.android.chrome", "Chrome"),
            AppInfo("com.facebook.katana", "Facebook"),
            AppInfo("com.instagram.android", "Instagram"),
            AppInfo("com.zhiliaoapp.musically", "TikTok"),
            AppInfo("com.whatsapp", "WhatsApp"),
            AppInfo("com.twitter.android", "Twitter"),
            AppInfo("com.snapchat.android", "Snapchat"),
            AppInfo("com.spotify.music", "Spotify"),
            AppInfo("com.netflix.mediaclient", "Netflix"),
            AppInfo("com.google.android.youtube", "YouTube")
        )
    }
}
