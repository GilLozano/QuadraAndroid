package com.example.quadraandroidstudio.data

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefono") val telefono: String
)