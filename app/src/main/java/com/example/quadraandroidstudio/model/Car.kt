package com.example.quadraandroidstudio.model

import com.google.gson.annotations.SerializedName

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Car(
    val id: Int,
    val marca: String,
    val modelo: String,
    val color: String,
    val anio: Int,
    val transmision: String,
    val tipo: String,
    val puertas: Int,
    val asientos: Int,
    val clima: Boolean,
    @SerializedName("precio_por_dia")
    val precioPorDia: Double,
    @SerializedName("seguroId")
    val seguroId: Int,
    val imagen: String?,
    val estado: String,
    val seguro: Seguro,
    val createdAt: String?,
    val updatedAt: String?
):Parcelable