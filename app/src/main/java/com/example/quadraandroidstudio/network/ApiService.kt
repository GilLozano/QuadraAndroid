package com.example.quadraandroidstudio.network


import com.example.quadraandroidstudio.data.ApiResponse
import com.example.quadraandroidstudio.data.CreateAccountRequest
import com.example.quadraandroidstudio.data.LoginRequest
import com.example.quadraandroidstudio.data.LoginResponse
import com.example.quadraandroidstudio.data.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // --- Autenticación ---

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/create-account")
    suspend fun createAccount(@Body request: CreateAccountRequest): ApiResponse

    // --- Usuarios ---

    @GET("api/users")
    suspend fun getUsers(): List<User>

}