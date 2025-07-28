package com.applock.vl.service

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.applock.vl.AuthActivity
import com.applock.vl.R
import com.applock.vl.utils.PrefsUtils

class AppMonitorService : Service() {
    
    private val handler = Handler(Looper.getMainLooper())
    private var monitoringRunnable: Runnable? = null
    
    private var lastForegroundApp = ""
    private val lockedApps = mutableSetOf<String>()
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "applock_service"
        private const val MONITORING_INTERVAL = 2000L // 2 seconds
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        loadLockedApps()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startMonitoring()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopMonitoring()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "app lock service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "dang chay applock"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("applock dang chay")
            .setContentText("bao ve ${lockedApps.size} app")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setOngoing(true)
            .build()
    }

    private fun loadLockedApps() {
        val apps = PrefsUtils.getLockedApps(this)
        lockedApps.clear()
        lockedApps.addAll(apps)
    }

    private fun startMonitoring() {
        monitoringRunnable = object : Runnable {
            override fun run() {
                checkForegroundApp()
                handler.postDelayed(this, MONITORING_INTERVAL)
            }
        }
        handler.post(monitoringRunnable!!)
    }

    private fun stopMonitoring() {
        monitoringRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun checkForegroundApp() {
        val currentApp = getCurrentForegroundApp()
        
        if (currentApp != lastForegroundApp && currentApp.isNotEmpty()) {
            if (lockedApps.contains(currentApp)) {
                showAuthScreen(currentApp)
            }
            lastForegroundApp = currentApp
        }
        
        // refresh locked apps periodically
        if (System.currentTimeMillis() % 10000 < MONITORING_INTERVAL) {
            loadLockedApps()
        }
    }

    private fun getCurrentForegroundApp(): String {
        return try {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val currentTime = System.currentTimeMillis()
            
            val events = usageStatsManager.queryEvents(
                currentTime - 5000,
                currentTime
            )
            
            var lastEvent: UsageEvents.Event? = null
            val event = UsageEvents.Event()
            
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    lastEvent = UsageEvents.Event().apply {
                        packageName = event.packageName
                        timeStamp = event.timeStamp
                        eventType = event.eventType
                    }
                }
            }
            
            lastEvent?.packageName ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun showAuthScreen(packageName: String) {
        try {
            val intent = Intent(this, AuthActivity::class.java).apply {
                putExtra("package_name", packageName)
                putExtra("app_name", getAppName(packageName))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun getAppName(packageName: String): String {
        return try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }
}
