package com.example.quadraandroidstudio.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {

    private const val PREF_NAME = "quadra_app_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"

    // Inicializa SharedPreferences
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // --- Token de Autenticación ---

    fun saveAuthToken(context: Context, token: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.apply()
    }

    fun getAuthToken(context: Context): String? {
        return getPreferences(context).getString(KEY_AUTH_TOKEN, null)
    }

    // --- ID del Usuario ---

    fun saveUserId(context: Context, userId: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(KEY_USER_ID, userId)
        editor.apply()
    }

    fun getUserId(context: Context): Int {
        // Devuelve -1 si no se encuentra ningún ID de usuario
        return getPreferences(context).getInt(KEY_USER_ID, -1)
    }

    // --- Limpiar Datos (Cerrar Sesión) ---

    fun clearData(context: Context) {
        val editor = getPreferences(context).edit()
        editor.clear()
        editor.apply()
    }
}