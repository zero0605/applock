package com.applock.vl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.applock.vl.ui.theme.AppLockTheme
import com.applock.vl.utils.AppUtils
import com.applock.vl.utils.PrefsUtils
import com.applock.vl.utils.ShizukuUtils
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppLockTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    var shizukuStatus by remember { mutableStateOf("checking...") }
    var currentScreen by remember { mutableStateOf("main") }

    LaunchedEffect(Unit) {
        scope.launch {
            shizukuStatus = ShizukuUtils.getShizukuStatus()
        }
    }

    when (currentScreen) {
        "main" -> MainScreenContent(
            shizukuStatus = shizukuStatus,
            onRefreshStatus = {
                scope.launch {
                    shizukuStatus = ShizukuUtils.getShizukuStatus()
                }
            },
            onRequestPermission = {
                scope.launch {
                    ShizukuUtils.requestShizukuPermission()
                    shizukuStatus = ShizukuUtils.getShizukuStatus()
                }
            },
            onContinueWithoutShizuku = {
                currentScreen = "applist"
            }
        )
        "applist" -> AppListScreen(
            onBack = { currentScreen = "main" }
        )
    }
}

@Composable
fun MainScreenContent(
    shizukuStatus: String,
    onRefreshStatus: () -> Unit,
    onRequestPermission: () -> Unit,
    onContinueWithoutShizuku: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // header
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "applock vl",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "app khoa ung dung",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // status
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "shizuku status:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("$shizukuStatus")

                if (shizukuStatus.contains("ok")) {
                    Text(
                        text = "✅ shizuku ready!",
                        color = androidx.compose.ui.graphics.Color.Green
                    )
                } else {
                    Text(
                        text = "⚠️ shizuku issue - co the dung basic mode",
                        color = androidx.compose.ui.graphics.Color.Red
                    )
                }
            }
        }

        // actions
        if (shizukuStatus.contains("ok")) {
            Button(
                onClick = onContinueWithoutShizuku,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("tiep tuc voi shizuku")
            }
        } else {
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("cap quyen shizuku")
            }

            Button(
                onClick = onRefreshStatus,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("refresh status")
            }

            Button(
                onClick = onContinueWithoutShizuku,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("tiep tuc khong can shizuku")
            }
        }
    }
}

@Composable
fun AppListScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var lockedApps by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                installedApps = AppUtils.getInstalledApps(context)
                if (installedApps.isEmpty()) {
                    // fallback to popular apps if can't get installed apps
                    installedApps = AppUtils.getPopularApps()
                }
                lockedApps = PrefsUtils.getLockedApps(context)
            } catch (e: Exception) {
                installedApps = AppUtils.getPopularApps()
                lockedApps = PrefsUtils.getLockedApps(context)
            }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // header
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "chon app can khoa",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "${lockedApps.size} app dang bi khoa",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // app list
        if (isLoading) {
            Card {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("dang tai danh sach app...")
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(installedApps) { app ->
                    AppItem(
                        app = app,
                        isLocked = lockedApps.contains(app.packageName),
                        onToggle = {
                            scope.launch {
                                if (lockedApps.contains(app.packageName)) {
                                    PrefsUtils.removeLockedApp(context, app.packageName)
                                } else {
                                    PrefsUtils.addLockedApp(context, app.packageName)
                                }
                                lockedApps = PrefsUtils.getLockedApps(context)
                            }
                        }
                    )
                }
            }
        }

        // back button
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("quay lai")
        }
    }
}

@Composable
fun AppItem(
    app: AppInfo,
    isLocked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isLocked,
                onCheckedChange = { onToggle() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }

            if (isLocked) {
                Text(
                    text = "🔒",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


