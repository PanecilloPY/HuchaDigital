package com.example.huchadigital.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.huchadigital.AppRoutes
import com.example.huchadigital.model.Hucha

@Composable
fun MainScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier // Apply the modifier passed from MainActivity here
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp), // Keep original internal padding
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(0.3f))

        Text(
            text = "Saldo: ${Hucha.saldo.value}€", // Assuming Hucha.saldo.value updates correctly
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(0.6f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { navController.navigate(AppRoutes.ADD_MONEY_SCREEN) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir Dinero")
            }
            Button(
                onClick = { navController.navigate(AppRoutes.REMOVE_MONEY_SCREEN) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quitar Dinero")
            }
            Button(
                onClick = { navController.navigate(AppRoutes.CONSULT_SCREEN) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Consultar Dinero")
            }
        }

        Spacer(Modifier.weight(0.8f))
    }
}
