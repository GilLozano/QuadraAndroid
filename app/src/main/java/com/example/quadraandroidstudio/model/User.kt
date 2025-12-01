package com.example.quadraandroidstudio.model


data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    // Otros campos opcionales que puedan venir
    val telefono: String?,
    val role: String?,
    val imagen: String?
)