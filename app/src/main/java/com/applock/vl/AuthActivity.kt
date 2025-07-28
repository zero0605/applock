package com.applock.vl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.applock.vl.ui.theme.AppLockTheme
import com.applock.vl.utils.PrefsUtils

class AuthActivity : FragmentActivity() {
    
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val targetPackage = intent.getStringExtra("package_name") ?: ""
        
        setupBiometric()
        
        setContent {
            AppLockTheme {
                AuthScreen(
                    targetPackage = targetPackage,
                    onAuthSuccess = { 
                        setResult(RESULT_OK)
                        finish()
                    },
                    onBiometricAuth = { showBiometricPrompt() }
                )
            }
        }
        
        if (isBiometricAvailable()) {
            showBiometricPrompt()
        }
    }

    private fun setupBiometric() {
        val executor = ContextCompat.getMainExecutor(this)
        
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    setResult(RESULT_OK)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_title))
            .setSubtitle(getString(R.string.biometric_subtitle))
            .setNegativeButtonText(getString(R.string.biometric_negative))
            .build()
    }

    private fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    private fun showBiometricPrompt() {
        biometricPrompt.authenticate(promptInfo)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    targetPackage: String,
    onAuthSuccess: () -> Unit,
    onBiometricAuth: () -> Unit
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
                    text = context.getString(R.string.auth_title),
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Text(
                    text = context.getString(R.string.auth_subtitle),
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = pin,
                    onValueChange = { 
                        pin = it
                        showError = false
                    },
                    label = { Text(context.getString(R.string.pin_hint)) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = showError,
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        text = context.getString(R.string.wrong_pin),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                        Text("xac nhan")
                    }

                    OutlinedButton(
                        onClick = onBiometricAuth,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("van tay")
                    }
                }
            }
        }
    }
}
