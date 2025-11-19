package com.example.quadraandroidstudio.data

import com.google.gson.annotations.SerializedName

// Este es el cuerpo que tu API espera para crear una reserva
data class ReservationRequest(
    @SerializedName("vehiculo_id")
    val vehiculoId: Int, // Debería ser Int para PostgreSQL
    @SerializedName("usuario_id")
    val usuarioId: Int, // Asumiendo que obtendrás el ID del usuario logueado
    val fechaInicio: String, // Formato "YYYY-MM-DD" o "DD/MM/YYYY" según tu API
    val fechaFin: String,
    val lugarRecogida: String,
    val metodoPago: String,
    val estado: String = "PENDIENTE" // Valor por defecto
)

// Clase para la respuesta simple de la API (si tu API devuelve id y mensaje)
data class CreateReservationResponse(
    val id: String, // El ID de la reserva creada
    val message: String
)