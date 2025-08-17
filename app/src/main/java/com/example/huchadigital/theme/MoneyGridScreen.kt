package com.example.huchadigital.theme

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.huchadigital.R
import com.example.huchadigital.model.Hucha
import kotlin.math.abs

data class MoneyItemDef(val claveDenominacion: Int, val imagenResId: Int, val displayText: String)

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyGridScreen(navController: NavController, isAdding: Boolean) {
    val context = LocalContext.current

    val initialBilletesMonedas = remember { Hucha.billetesMonedas.toMap() }
    var hasMadeChanges by remember { mutableStateOf(false) }

    val pendingChanges = remember { mutableStateMapOf<Int, Int>() }

    var showDiscardDialog by remember { mutableStateOf(false) }
    var dontShowDiscardDialogAgainPreference by remember { mutableStateOf(false) }

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

    val itemsAMostrar = remember(isAdding, initialBilletesMonedas, pendingChanges.toMap()) {
        if (!isAdding) {
            todasDenominacionesConDetalles.filter { def ->
                (initialBilletesMonedas[def.claveDenominacion] ?: 0) > 0
            }
        } else {
            todasDenominacionesConDetalles
        }
    }

    fun applyPendingChangesToHucha() {
        pendingChanges.forEach { (denomination, netChange) ->
            if (netChange > 0) {
                repeat(netChange) { Hucha.agregar(denomination) }
            } else if (netChange < 0) {
                repeat(abs(netChange)) { Hucha.quitar(denomination) }
            }
        }
        pendingChanges.clear()
        hasMadeChanges = false
    }

    fun discardPendingChanges() {
        pendingChanges.clear()
        hasMadeChanges = false
    }

    fun handleBackNavigation() {
        if (hasMadeChanges) {
            if (dontShowDiscardDialogAgainPreference) {
                discardPendingChanges()
                navController.popBackStack()
            } else {
                showDiscardDialog = true
            }
        } else {
            navController.popBackStack()
        }
    }

    BackHandler(enabled = true) {
        handleBackNavigation()
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Descartar Cambios") },
            text = {
                Column {
                    Text("Tienes cambios sin aplicar. ¿Quieres salir y descartarlos?")
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = dontShowDiscardDialogAgainPreference,
                            onCheckedChange = { dontShowDiscardDialogAgainPreference = it }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("No volver a mostrar")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        discardPendingChanges()
                        navController.popBackStack()
                    }
                ) { Text("Descartar") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isAdding) "Añadir Dinero" else "Quitar Dinero") },
                navigationIcon = {
                    IconButton(onClick = { handleBackNavigation() }) {
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
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isAdding && itemsAMostrar.isEmpty() && !hasMadeChanges) {
                Spacer(Modifier.weight(1f))
                Text("No tienes dinero de estas denominaciones para quitar.")
                Spacer(Modifier.weight(1f))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(itemsAMostrar.size) { index ->
                        val item = itemsAMostrar[index]
                        val initialCantidad = initialBilletesMonedas[item.claveDenominacion] ?: 0
                        val pendingChange = pendingChanges[item.claveDenominacion] ?: 0
                        val effectiveCantidad = initialCantidad + pendingChange

                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = item.imagenResId),
                                    contentDescription = "Imagen de ${item.displayText}",
                                    modifier = Modifier.size(70.dp)
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(item.displayText, style = MaterialTheme.typography.titleMedium)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("Tienes: $initialCantidad", style = MaterialTheme.typography.bodySmall)
                                    if (pendingChange != 0) {
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = String.format("(%+d)", pendingChange),
                                            color = if (pendingChange > 0) Color(0xFF006400) else Color.Red,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Button(onClick = {
                                    hasMadeChanges = true
                                    if (isAdding) {
                                        pendingChanges[item.claveDenominacion] = pendingChange + 1
                                    } else {
                                        if (effectiveCantidad > 0) {
                                            pendingChanges[item.claveDenominacion] = pendingChange - 1
                                        } else {
                                            Toast.makeText(context, "No puedes quitar más de ${item.displayText}", Toast.LENGTH_SHORT).show()
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
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    applyPendingChangesToHucha()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hecho")
            }
        }
    }
}
