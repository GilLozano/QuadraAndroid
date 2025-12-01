package com.example.quadraandroidstudio.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reservation(
    @SerializedName("_id") val id: String, // El ID de MongoDB es un string "_id"
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("fecha_inicio") val fechaInicio: String, // Vienen como strings ISO
    @SerializedName("fecha_fin") val fechaFin: String,
    val estado: String, // Ej: "Vehiculo En Proceso de Entrega", "Finalizada"
    val nombre: String?, // Asumo que se guardan con estos nombres simples
    val email: String?,
    val telefono: String?, // Puede ser nulo
    @SerializedName("lugar_recogida") val lugarRecogida: String?,
    val alquiler: AlquilerDetails, // Objeto anidado de Mongo
    val vehiculo: VehiculoDetails? // Objeto anidado de Postgres (puede ser nulo si se borró el auto)
): Parcelable

@Parcelize
data class AlquilerDetails(
    val monto: Double,
    @SerializedName("metodo_pago") val metodoPago: String,
    val estado: String // Ej: "No Pagado", "Pagado"
): Parcelable

@Parcelize
data class VehiculoDetails(
    val id: Int,
    val marca: String,
    val modelo: String,
    val imagen: String?, // Puede ser nulo
    val anio: Int
    // Agrega más si tu backend los está enviando y los necesitas en la UI
):Parcelable