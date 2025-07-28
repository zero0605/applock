package com.applock.vl.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
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
import kotlinx.coroutines.*

class AppMonitorService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val handler = Handler(Looper.getMainLooper())
    private var monitoringRunnable: Runnable? = null
    
    private var lastForegroundApp = ""
    private val lockedApps = mutableSetOf<String>()
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "applock_service"
        private const val MONITORING_INTERVAL = 1000L
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
        serviceScope.cancel()
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
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    private fun loadLockedApps() {
        serviceScope.launch {
            val apps = PrefsUtils.getLockedApps(this@AppMonitorService)
            lockedApps.clear()
            lockedApps.addAll(apps)
        }
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
        
        if (currentApp != lastForegroundApp) {
            if (lockedApps.contains(currentApp)) {
                showAuthScreen(currentApp)
            }
            lastForegroundApp = currentApp
        }
    }

    private fun getCurrentForegroundApp(): String {
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
                lastEvent = UsageEvents.Event(event)
            }
        }
        
        return lastEvent?.packageName ?: ""
    }

    private fun showAuthScreen(packageName: String) {
        val intent = Intent(this, AuthActivity::class.java).apply {
            putExtra("package_name", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
    }
}
