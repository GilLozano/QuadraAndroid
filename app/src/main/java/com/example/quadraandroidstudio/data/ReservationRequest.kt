package com.example.quadraandroidstudio.data

import com.google.gson.annotations.SerializedName

// Clase principal para la solicitud
data class ReservationRequest(
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("vehiculo_id") val vehiculoId: Int,
    val nombre: String,     // Requerido por tu esquema Mongoose
    val telefono: String,   // Requerido por tu esquema Mongoose
    val email: String,      // Requerido por tu esquema Mongoose
    @SerializedName("fecha_inicio") val fechaInicio: String, // Formato YYYY-MM-DD
    @SerializedName("fecha_fin") val fechaFin: String,       // Formato YYYY-MM-DD
    val alquiler: AlquilerData, // Objeto anidado requerido
    val lugarRecogida: String // Aunque no está en tu esquema Mongoose, estaba en tu form anterior. Lo mantengo por si acaso tu backend lo usa de alguna forma no visible aquí, si no, puedes quitarlo.
)

// Sub-clase para los datos de alquiler anidados
data class AlquilerData(
    val monto: Double,
    @SerializedName("metodo_pago") val metodoPago: String
    // 'estado' tiene un default en Mongoose ("No Pagado"), no hace falta enviarlo.
)