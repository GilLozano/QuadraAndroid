package com.example.quadraandroidstudio.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sucursal(
    val id: Int,
    val nombre: String,
    @SerializedName("direccion_completa") // Ajusta si tu API usa otro nombre
    val direccion: String,
    val telefono: String?,
    // Añade otros campos que devuelva tu API si los necesitas
    val createdAt: String?,
    val updatedAt: String?
) : Parcelable {
    // Sobreescribimos toString para que el AutoCompleteTextView muestre el nombre
    override fun toString(): String {
        return nombre
    }
}