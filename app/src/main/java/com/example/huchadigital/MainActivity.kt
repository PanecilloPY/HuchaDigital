package com.example.huchadigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.huchadigital.screens.ConsultScreen
import com.example.huchadigital.screens.MainScreen
// import com.example.huchadigital.screens.ModifyScreen
import com.example.huchadigital.theme.MoneyGridScreen // << IMPORTACIÓN ACTUALIZADA

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Es una buena práctica llamar a Hucha.init() aquí si aún no lo has hecho
        // Hucha.init(applicationContext) 
        setContent {
            _root_ide_package_.com.example.huchadigital.theme.HuchaDigitalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("add") { MoneyGridScreen(navController, true) }
        composable("remove") { MoneyGridScreen(navController, false) }
        composable("consult") { ConsultScreen(navController) }
    }
}

