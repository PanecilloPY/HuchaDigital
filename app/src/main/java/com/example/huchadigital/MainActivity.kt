package com.example.huchadigital

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.huchadigital.model.Hucha
import com.example.huchadigital.screens.ConsultScreen
import com.example.huchadigital.screens.MainScreen
import com.example.huchadigital.theme.AuthPrefs
import com.example.huchadigital.theme.AuthScreen
import com.example.huchadigital.theme.CreatePasswordScreen
import com.example.huchadigital.theme.HuchaDigitalTheme
import com.example.huchadigital.theme.MoneyGridScreen

object AppRoutes {
    const val AUTH_CHECK_SCREEN = "auth_check_screen"
    const val AUTH_SCREEN = "auth_screen"
    const val MAIN_APP_CONTENT_SCREEN = "main_app_content_screen"
    const val CREATE_PASSWORD_SCREEN = "create_password_screen"
    const val ADD_MONEY_SCREEN = "add_money"
    const val REMOVE_MONEY_SCREEN = "remove_money"
    const val CONSULT_SCREEN = "consult"
}

class MainActivity : AppCompatActivity() { // CHANGED
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Hucha.init(applicationContext)
        setContent {
            HuchaDigitalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigationHost()
                }
            }
        }
    }
}

@Composable
fun AppNavigationHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val startDestination = AppRoutes.AUTH_CHECK_SCREEN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppRoutes.AUTH_CHECK_SCREEN) {
            LaunchedEffect(Unit) {
                if (AuthPrefs.isPasswordSet(context)) {
                    navController.navigate(AppRoutes.AUTH_SCREEN) {
                        popUpTo(AppRoutes.AUTH_CHECK_SCREEN) { inclusive = true }
                    }
                } else {
                    navController.navigate(AppRoutes.MAIN_APP_CONTENT_SCREEN) {
                        popUpTo(AppRoutes.AUTH_CHECK_SCREEN) { inclusive = true }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        composable(AppRoutes.AUTH_SCREEN) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(AppRoutes.MAIN_APP_CONTENT_SCREEN) {
                        popUpTo(AppRoutes.AUTH_SCREEN) { inclusive = true }
                    }
                },
                onAuthFailedMessage = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            )
        }

        composable(AppRoutes.CREATE_PASSWORD_SCREEN) {
            CreatePasswordScreen(
                onPasswordCreated = {
                    Toast.makeText(context, "Contraseña creada", Toast.LENGTH_SHORT).show()
                    navController.navigate(AppRoutes.MAIN_APP_CONTENT_SCREEN) {
                        popUpTo(AppRoutes.CREATE_PASSWORD_SCREEN) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.MAIN_APP_CONTENT_SCREEN) {
            MainAppContent(navController)
        }
        composable(AppRoutes.ADD_MONEY_SCREEN) { MoneyGridScreen(navController, true) }
        composable(AppRoutes.REMOVE_MONEY_SCREEN) { MoneyGridScreen(navController, false) }
        composable(AppRoutes.CONSULT_SCREEN) { ConsultScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(navController: NavHostController) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var isPasswordSet by remember { mutableStateOf(AuthPrefs.isPasswordSet(context)) }

    LaunchedEffect(AuthPrefs.isPasswordSet(context)) {
        isPasswordSet = AuthPrefs.isPasswordSet(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hucha Digital") },
                actions = {
                    if (!isPasswordSet) {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Opciones")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Crear Contraseña") },
                                onClick = {
                                    showMenu = false
                                    navController.navigate(AppRoutes.CREATE_PASSWORD_SCREEN)
                                }
                            )
                        }
                    }
                    if (isPasswordSet) {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Opciones Dev")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("DEBUG: Borrar Contraseña") },
                                onClick = {
                                    showMenu = false
                                    AuthPrefs.clearPassword(context)
                                    navController.navigate(AppRoutes.AUTH_CHECK_SCREEN) {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        MainScreen(navController = navController, modifier = Modifier.padding(paddingValues))
    }
}
