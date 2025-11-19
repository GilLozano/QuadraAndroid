package com.example.quadraandroidstudio.network

import com.example.quadraandroidstudio.data.ApiResponse
import com.example.quadraandroidstudio.data.CreateAccountRequest
import com.example.quadraandroidstudio.data.CreateReservationResponse
import com.example.quadraandroidstudio.data.EmailRequest
import com.example.quadraandroidstudio.data.LoginRequest
import com.example.quadraandroidstudio.data.LoginResponse
import com.example.quadraandroidstudio.data.NewPasswordRequest
import com.example.quadraandroidstudio.data.ReservationRequest
import com.example.quadraandroidstudio.data.TokenRequest
import com.example.quadraandroidstudio.data.User
import com.example.quadraandroidstudio.model.Car
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // --- Autenticación ---

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): String

    @POST("api/auth/create-account")
    suspend fun createAccount(@Body request: CreateAccountRequest): ApiResponse

    // --- Vehículos ---
    @GET("api/cars")
    suspend fun getVehiculos(): List<Car>

    // --- Reservas ---
    @POST("api/reservations")
    suspend fun createReservation(@Body request: ReservationRequest): CreateReservationResponse

    // --- Usuarios ---

    @GET("api/users")
    suspend fun getUsers(): List<User>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: EmailRequest): String

    @POST("api/auth/validate-token")
    suspend fun validateToken(@Body request: TokenRequest): ApiResponse

    @POST("api/auth/update-password/{token}")
    suspend fun setNewPasswordWithToken(
        @Path("token") token: String,
        @Body request: NewPasswordRequest
    ): ApiResponse
}