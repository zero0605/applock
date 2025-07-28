package com.applock.vl

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.applock.vl.data.AppInfo
import com.applock.vl.service.AppMonitorService
import com.applock.vl.ui.theme.AppLockTheme
import com.applock.vl.utils.ShizukuUtils
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { checkPermissions() }
    
    private val usageStatsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { checkPermissions() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AppLockTheme {
                MainScreen(
                    onRequestPermissions = { requestPermissions() },
                    onStartService = { startMonitorService() }
                )
            }
        }
    }

    private fun requestPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
        } else if (!hasUsageStatsPermission()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            usageStatsLauncher.launch(intent)
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun checkPermissions() {
        // refresh ui state
    }

    private fun startMonitorService() {
        val intent = Intent(this, AppMonitorService::class.java)
        startForegroundService(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onRequestPermissions: () -> Unit,
    onStartService: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var shizukuStatus by remember { mutableStateOf("checking...") }
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var selectedApps by remember { mutableStateOf<Set<String>>(emptySet()) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            shizukuStatus = when {
                !ShizukuUtils.isShizukuRunning() -> "shizuku chua chay"
                !ShizukuUtils.hasShizukuPermission() -> "chua cap quyen shizuku"
                else -> "shizuku ok"
            }
            
            installedApps = getInstalledApps(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("trang thai:", style = MaterialTheme.typography.titleMedium)
                Text("shizuku: $shizukuStatus")
                Text("overlay: ${if (Settings.canDrawOverlays(context)) "ok" else "chua cap"}")
                Text("usage stats: ${if (hasUsageStatsPermission(context)) "ok" else "chua cap"}")
            }
        }

        Button(
            onClick = onRequestPermissions,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("cap quyen")
        }

        Text("chon app can khoa:", style = MaterialTheme.typography.titleMedium)
        
        LazyColumn {
            items(installedApps) { app ->
                AppItem(
                    app = app,
                    isSelected = selectedApps.contains(app.packageName),
                    onToggle = { 
                        selectedApps = if (selectedApps.contains(app.packageName)) {
                            selectedApps - app.packageName
                        } else {
                            selectedApps + app.packageName
                        }
                    }
                )
            }
        }

        Button(
            onClick = onStartService,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedApps.isNotEmpty()
        ) {
            Text("bat dau khoa app")
        }
    }
}

@Composable
fun AppItem(
    app: AppInfo,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(app.name)
        }
    }
}

private fun getInstalledApps(context: Context): List<AppInfo> {
    val pm = context.packageManager
    return pm.getInstalledApplications(PackageManager.GET_META_DATA)
        .filter { it.packageName != context.packageName }
        .map { appInfo ->
            AppInfo(
                packageName = appInfo.packageName,
                name = pm.getApplicationLabel(appInfo).toString()
            )
        }
        .sortedBy { it.name }
}

private fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}
