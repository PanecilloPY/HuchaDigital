package com.example.huchadigital.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.huchadigital.R
import com.example.huchadigital.model.Hucha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyScreen(navController: NavHostController, isAdding: Boolean) {
    val context = LocalContext.current

    val todosValores = listOf(1, 2, 5, 10, 20, 50, 100, 200)
    val todasImagenes = listOf(
        R.drawable.moneda_1, R.drawable.moneda_2,
        R.drawable.billete_5, R.drawable.billete_10,
        R.drawable.billete_20, R.drawable.billete_50,
        R.drawable.billete_100, R.drawable.billete_200
    )

    val itemsAMostrar = remember(isAdding, Hucha.billetesMonedas.entries.toList()) {
        if (!isAdding) {
            todosValores.indices
                .filter { index -> (Hucha.billetesMonedas[todosValores[index]] ?: 0) > 0 }
                .map { index -> Pair(todosValores[index], todasImagenes[index]) }
        } else {
            todosValores.indices.map { index -> Pair(todosValores[index], todasImagenes[index]) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isAdding) "Añadir Dinero" else "Quitar Dinero") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isAdding && itemsAMostrar.isEmpty()) {
                Text("No tienes dinero para quitar.")
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
                    items(itemsAMostrar.size) { index ->
                        val (valor, imagenId) = itemsAMostrar[index]
                        val cantidadActual = Hucha.billetesMonedas[valor] ?: 0

                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = imagenId),
                                    contentDescription = "Imagen de ${valor}€",
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("${valor}€")
                                Text("Tienes: $cantidadActual")
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = {
                                    if (isAdding) {
                                        Hucha.agregar(valor)
                                    } else {
                                        val exito = Hucha.quitar(valor)
                                        if (!exito) {
                                            Toast.makeText(
                                                context,
                                                "No quedan billetes/monedas de ${valor}€",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }) {
                                    Text(if (isAdding) "Añadir" else "Quitar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
