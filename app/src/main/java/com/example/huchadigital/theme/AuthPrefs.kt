package com.example.huchadigital.theme

import android.content.Context
import android.content.SharedPreferences

object AuthPrefs {
    private const val PREFS_NAME = "AppAuthPrefs"
    private const val KEY_IS_PASSWORD_SET = "is_password_set"
    private const val KEY_PASSWORD_STORE = "password_store"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isPasswordSet(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_PASSWORD_SET, false)
    }

    fun setPassword(context: Context, password: String) {
        getPrefs(context).edit()
            .putBoolean(KEY_IS_PASSWORD_SET, true)
            .putString(KEY_PASSWORD_STORE, password)
            .apply()
    }

    fun verifyPassword(context: Context, enteredPassword: String): Boolean {
        val storedPasswordRepresentation = getPrefs(context).getString(KEY_PASSWORD_STORE, null)
        return storedPasswordRepresentation != null && storedPasswordRepresentation == enteredPassword
    }

    fun clearPassword(context: Context) {
        getPrefs(context).edit()
            .remove(KEY_IS_PASSWORD_SET)
            .remove(KEY_PASSWORD_STORE)
            .apply()
    }
}