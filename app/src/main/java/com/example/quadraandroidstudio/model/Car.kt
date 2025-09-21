package com.example.quadraandroidstudio.model // Asegúrate de que este sea tu paquete real

data class Car(
    val id: String,
    val name: String,
    val brand: String,
    val year: Int,
    val pricePerDay: Double,
    val imageUrl: String // Esto será el nombre del drawable
)
