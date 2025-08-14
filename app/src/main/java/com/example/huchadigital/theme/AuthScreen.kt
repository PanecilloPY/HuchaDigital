package com.example.huchadigital.theme

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

private fun Context.findFragmentActivity(): FragmentActivity? {
    var currentContext = this
    android.util.Log.d("AuthScreenDebug", "Starting findFragmentActivity with context: ${currentContext.javaClass.name}")
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) {
            android.util.Log.d("AuthScreenDebug", "Found FragmentActivity: ${currentContext.javaClass.name}")
            return currentContext
        }
        currentContext = currentContext.baseContext
        android.util.Log.d("AuthScreenDebug", "Next context in wrapper: ${currentContext.javaClass.name}")
    }
    android.util.Log.d("AuthScreenDebug", "FragmentActivity not found, returning null. Final context: ${currentContext.javaClass.name}")
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onAuthFailedMessage: (String) -> Unit = {}
) {
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    android.util.Log.d("AuthScreenDebug", "LocalContext.current in AuthScreen: ${context.javaClass.name}")
    val activity = remember(context) {
        val foundActivity = context.findFragmentActivity()
        android.util.Log.d("AuthScreenDebug", "Result of findFragmentActivity in remember: ${foundActivity?.javaClass?.name}")
        foundActivity
    }

    var biometricState by remember {
        mutableStateOf(BiometricState.UNCHECKED)
    }

    val biometricEnrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        biometricState = BiometricState.CHECK_AGAIN // Trigger re-evaluation
    }

    val biometricSystemInstances = remember(activity) {
        if (activity != null) {
            val executor = ContextCompat.getMainExecutor(activity)
            val biometricPromptCallback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        onAuthFailedMessage("Error de autenticación: $errString")
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onAuthSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            }
            val biometricPrompt = BiometricPrompt(activity, executor, biometricPromptCallback)
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación Biométrica")
                .setSubtitle("Usa tu huella para desbloquear")
                .setNegativeButtonText("Usar Contraseña")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .build()
            Pair(biometricPrompt, promptInfo)
        } else {
            null
        }
    }

    LaunchedEffect(activity, biometricState) {
        if (activity == null) {
            val biometricManager = BiometricManager.from(context)
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
                biometricState = BiometricState.AVAILABLE_NO_ACTIVITY
            } else {
                biometricState = BiometricState.NOT_AVAILABLE
            }
            return@LaunchedEffect
        }

        val biometricManager = BiometricManager.from(activity) // Use activity context if available
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> biometricState = BiometricState.AVAILABLE_READY
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> biometricState = BiometricState.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> biometricState = BiometricState.HW_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                biometricState = BiometricState.NONE_ENROLLED
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
                        )
                    }
                    biometricEnrollLauncher.launch(enrollIntent)
                }
            }
            else -> biometricState = BiometricState.NOT_AVAILABLE
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Ingresar Contraseña", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (AuthPrefs.verifyPassword(context, password)) {
                    onAuthSuccess()
                } else {
                    onAuthFailedMessage("Contraseña incorrecta.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Desbloquear con Contraseña")
        }

        when (biometricState) {
            BiometricState.AVAILABLE_READY -> {
                if (biometricSystemInstances != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { biometricSystemInstances.first.authenticate(biometricSystemInstances.second) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Usar Huella Dactilar")
                    }
                }
            }
            BiometricState.AVAILABLE_NO_ACTIVITY -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text("La autenticación biométrica podría estar disponible pero no se pudo inicializar.", style = MaterialTheme.typography.bodySmall)
            }
            BiometricState.NONE_ENROLLED -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No hay huellas configuradas. Puede configurarlas desde los Ajustes del sistema.", style = MaterialTheme.typography.bodySmall)
                }
            }
            else -> { /* No button or specific message for other states like NO_HARDWARE, HW_UNAVAILABLE, NOT_AVAILABLE */ }
        }
    }
}

private enum class BiometricState {
    UNCHECKED,
    CHECK_AGAIN,
    AVAILABLE_READY,
    AVAILABLE_NO_ACTIVITY,
    NONE_ENROLLED,
    NO_HARDWARE,
    HW_UNAVAILABLE,
    NOT_AVAILABLE
}
