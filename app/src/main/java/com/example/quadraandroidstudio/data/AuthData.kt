package com.example.quadraandroidstudio.data

import com.google.gson.annotations.SerializedName

// Datos que envías a la API de Login
data class LoginRequest(
    val email: String,
    val password: String
)

// Datos que recibes de la API de Login
data class LoginResponse(
    val token: String
)

//modelos para Crear Cuenta, etc.
data class CreateAccountRequest(
    val nombre: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation") // Para que coincida con el JSON
    val passwordConfirmation: String
)

data class ApiResponse(
    val mensaje: String
)

data class EmailRequest(val email: String)

// For validateToken API
data class TokenRequest(val token: String)

// For setNewPasswordWithToken API
data class NewPasswordRequest(val password: String)

data class ErrorResponse(val error: String)