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
import com.example.huchadigital.theme.MoneyItemDef

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultScreen(navController: NavHostController) {

    val todasDenominacionesConDetalles = remember {
        listOf(
            MoneyItemDef(0, R.drawable.moneda_50cent, "0.50€"),
            MoneyItemDef(1, R.drawable.moneda_1, "1€"),
            MoneyItemDef(2, R.drawable.moneda_2, "2€"),
            MoneyItemDef(5, R.drawable.billete_5, "5€"),
            MoneyItemDef(10, R.drawable.billete_10, "10€"),
            MoneyItemDef(20, R.drawable.billete_20, "20€"),
            MoneyItemDef(50, R.drawable.billete_50, "50€"),
            MoneyItemDef(100, R.drawable.billete_100, "100€"),
            MoneyItemDef(200, R.drawable.billete_200, "200€"),
            MoneyItemDef(500, R.drawable.billete_500, "500€")
        )
    }

    val itemsExistentes = remember(Hucha.billetesMonedas.toMap()) {
        todasDenominacionesConDetalles.mapNotNull { moneyItemDef ->
            val cantidad = Hucha.billetesMonedas[moneyItemDef.claveDenominacion] ?: 0
            if (cantidad > 0) {
                Pair(moneyItemDef, cantidad)
            } else {
                null
            }
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
                        val (moneyItem, cantidad) = itemsExistentes[index]
                        val imagenId = moneyItem.imagenResId
                        val textoDenominacion = moneyItem.displayText

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
                                    contentDescription = "Imagen de $textoDenominacion",
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "$cantidad x $textoDenominacion",
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
