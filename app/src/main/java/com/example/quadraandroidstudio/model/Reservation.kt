package com.example.quadraandroidstudio.model


import com.example.quadraandroidstudio.model.Car

data class Reservation(
    val id: String,
    val car: Car, // Referencia al objeto Car
    val startDate: String,
    val endDate: String,
    val status: String, // "PENDIENTE", "COMPLETADA", etc.
    val imageUrl: String // URL o nombre del drawable para la imagen del coche en esta reserva
)