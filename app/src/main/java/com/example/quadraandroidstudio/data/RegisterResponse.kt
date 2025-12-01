package com.example.quadraandroidstudio.data

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("token") val token: String,
    @SerializedName("userId") val userId: Int
)