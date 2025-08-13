package com.example.huchadigital.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.huchadigital.R
import com.example.huchadigital.model.Hucha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultScreen(navController: NavHostController) {
    val todosValores = listOf(1, 2, 5, 10, 20, 50, 100, 200)
    val todasImagenes = listOf(
        R.drawable.moneda_1, R.drawable.moneda_2,
        R.drawable.billete_5, R.drawable.billete_10,
        R.drawable.billete_20, R.drawable.billete_50,
        R.drawable.billete_100, R.drawable.billete_200
    )

    val itemsExistentes = remember(Hucha.billetesMonedas.entries.toList()) {
        todosValores.indices
            .filter { index -> (Hucha.billetesMonedas[todosValores[index]] ?: 0) > 0 }
            .map { index ->
                val valor = todosValores[index]
                Triple(
                    valor,
                    todasImagenes[index],
                    Hucha.billetesMonedas[valor]!!
                )
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consultar Saldo") },
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
            Text("Saldo total: ${Hucha.saldo.value}€", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(20.dp))

            if (itemsExistentes.isEmpty()) {
                Text("Tu hucha está vacía.")
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
                    items(itemsExistentes.size) { index ->
                        val (valor, imagenId, cantidad) = itemsExistentes[index]
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = imagenId),
                                    contentDescription = "Imagen de ${valor}€",
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "$cantidad x ${valor}€",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
