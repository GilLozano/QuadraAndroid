package com.example.quadraandroidstudio.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Seguro(
    val id: Int,
    val tipo: String,
    val cobertura: String,
    val precio: Double,
    val descripcion: String
):Parcelable