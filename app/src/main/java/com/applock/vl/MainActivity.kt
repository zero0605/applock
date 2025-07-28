package com.applock.vl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.applock.vl.ui.theme.AppLockTheme
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
                        color = androidx.compose.ui.graphics.Color.Orange
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
                Text(
                    text = "chon app can khoa",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "basic mode - khong can shizuku",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📱 chrome")
                Text("📱 facebook")
                Text("📱 instagram")
                Text("📱 tiktok")
                Text("...")
                Text("(danh sach app se duoc implement sau)")
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("quay lai")
        }
    }
}


