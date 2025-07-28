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

    LaunchedEffect(Unit) {
        scope.launch {
            shizukuStatus = ShizukuUtils.getShizukuStatus()
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
                    text = "applock vl",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "app khoa ung dung bang shizuku",
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
                    text = "trang thai:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("shizuku: $shizukuStatus")
            }
        }

        // actions
        Button(
            onClick = {
                scope.launch {
                    if (!ShizukuUtils.hasShizukuPermission()) {
                        ShizukuUtils.requestShizukuPermission()
                    }
                    shizukuStatus = ShizukuUtils.getShizukuStatus()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("cap quyen shizuku")
        }

        Button(
            onClick = {
                scope.launch {
                    shizukuStatus = ShizukuUtils.getShizukuStatus()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("refresh status")
        }
    }
}


