package com.example.quadraandroidstudio.data

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name") val nombre: String,

    val email: String,
    val telefono: String,
    val password: String,

    // Agregamos el campo de confirmación que exige el backend
    @SerializedName("password_confirmation") val passwordConfirmation: String
)