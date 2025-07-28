package com.applock.vl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.applock.vl.ui.theme.AppLockTheme
import com.applock.vl.utils.PrefsUtils

class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val targetPackage = intent.getStringExtra("package_name") ?: ""
        val appName = intent.getStringExtra("app_name") ?: "app"
        
        setContent {
            AppLockTheme {
                AuthScreen(
                    appName = appName,
                    onAuthSuccess = { 
                        setResult(RESULT_OK)
                        finish()
                    },
                    onAuthCancel = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    appName: String,
    onAuthSuccess: () -> Unit,
    onAuthCancel: () -> Unit
) {
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    
    val savedPin = remember { PrefsUtils.getPin(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🔒",
                    style = MaterialTheme.typography.headlineLarge
                )
                
                Text(
                    text = "app bi khoa",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Text(
                    text = "nhap pin de mo khoa $appName",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = pin,
                    onValueChange = { 
                        pin = it
                        showError = false
                    },
                    label = { Text("nhap pin") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = showError,
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        text = "sai pin roi",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onAuthCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("huy")
                    }
                    
                    Button(
                        onClick = {
                            if (pin == savedPin) {
                                onAuthSuccess()
                            } else {
                                showError = true
                                pin = ""
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("mo khoa")
                    }
                }
                
                Text(
                    text = "pin mac dinh: 1234",
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
        }
    }
}
