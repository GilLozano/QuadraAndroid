package com.example.quadraandroidstudio.data

// Representa un objeto Usuario como lo devuelve tu API
data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val role: String,
    val imagen: String?, // Puede ser nulo
    val confirmado: Boolean,
    val createdAt: String,
    val updatedAt: String
)