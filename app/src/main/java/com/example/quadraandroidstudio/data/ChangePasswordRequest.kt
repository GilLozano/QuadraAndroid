package com.example.quadraandroidstudio.data

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("userId") val userId: Int,
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String
)