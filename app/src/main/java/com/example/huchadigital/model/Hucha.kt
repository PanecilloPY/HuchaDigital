package com.example.huchadigital.model

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Hucha {
    var saldo = mutableStateOf(0.0)
        private set

    var billetesMonedas = mutableStateMapOf<Int, Int>()
        private set

    private const val PREFS_NAME = "HuchaPrefs"
    private const val SALDO_KEY = "saldo"
    private const val BILLETES_MONEDAS_KEY = "billetesMonedas"

    val DENOMINATIONS = listOf(500, 200, 100, 50, 20, 10, 5, 2, 1, 0)

    private lateinit var preferences: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadData()
    }

    private fun saveData() {
        val editor = preferences.edit()
        editor.putString(SALDO_KEY, saldo.value.toString())
        val billetesMonedasJson = gson.toJson(billetesMonedas.toMap())
        editor.putString(BILLETES_MONEDAS_KEY, billetesMonedasJson)
        editor.apply()
    }

    private fun loadData() {
        saldo.value = preferences.getString(SALDO_KEY, "0.0")?.toDoubleOrNull() ?: 0.0
        val billetesMonedasJson = preferences.getString(BILLETES_MONEDAS_KEY, null)
        billetesMonedas.clear()
        if (billetesMonedasJson != null) {
            val type = object : TypeToken<Map<Int, Int>>() {}.type
            val loadedMap: Map<Int, Int> = gson.fromJson(billetesMonedasJson, type)
            DENOMINATIONS.forEach { denomination ->
                billetesMonedas[denomination] = loadedMap[denomination] ?: 0
            }
        } else {
            DENOMINATIONS.forEach { denomination ->
                billetesMonedas[denomination] = 0
            }
        }
    }

    fun agregar(valorDenominacion: Int) {
        val currentCount = billetesMonedas[valorDenominacion] ?: 0
        billetesMonedas[valorDenominacion] = currentCount + 1
        val valorRealParaSaldo = if (valorDenominacion == 0) 0.5 else valorDenominacion.toDouble()
        saldo.value += valorRealParaSaldo
        saveData()
    }

    fun quitar(valorDenominacion: Int): Boolean {
        val currentCount = billetesMonedas[valorDenominacion] ?: 0
        return if (currentCount > 0) {
            billetesMonedas[valorDenominacion] = currentCount - 1
            val valorRealParaSaldo = if (valorDenominacion == 0) 0.5 else valorDenominacion.toDouble()
            saldo.value -= valorRealParaSaldo
            saveData()
            true
        } else {
            false
        }
    }

    fun getSaldoDisplay(): String {
        return String.format("%.2f€", saldo.value)
    }

    fun getCantidadForDenominacion(denominacion: Int): Int {
        return billetesMonedas[denominacion] ?: 0
    }

    fun restoreStateAndSave(newBilletesMonedas: Map<Int, Int>, newSaldo: Double) {
        billetesMonedas.clear()
        newBilletesMonedas.forEach { (key, value) ->
            billetesMonedas[key] = value
        }
        DENOMINATIONS.forEach { knownDenomination ->
            if (!billetesMonedas.containsKey(knownDenomination)) {
                billetesMonedas[knownDenomination] = 0
            }
        }
        saldo.value = newSaldo
        saveData()
    }
}
