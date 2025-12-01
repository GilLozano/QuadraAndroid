package com.example.quadraandroidstudio.network

import com.example.quadraandroidstudio.data.ApiResponse
import com.example.quadraandroidstudio.data.UpdateProfileRequest
import com.example.quadraandroidstudio.data.CreateAccountRequest
import com.example.quadraandroidstudio.data.CreateReservationResponse
import com.example.quadraandroidstudio.data.EmailRequest
import com.example.quadraandroidstudio.data.LoginRequest
import com.example.quadraandroidstudio.data.LoginResponse
import com.example.quadraandroidstudio.data.RegisterRequest
import com.example.quadraandroidstudio.data.ChangePasswordRequest
import com.example.quadraandroidstudio.data.RegisterResponse
import com.example.quadraandroidstudio.data.NewPasswordRequest
import com.example.quadraandroidstudio.data.ReservationRequest
import com.example.quadraandroidstudio.data.TokenRequest
import com.example.quadraandroidstudio.model.User
import com.example.quadraandroidstudio.model.Car
import com.example.quadraandroidstudio.model.Sucursal
import com.example.quadraandroidstudio.model.Reservation
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // --- Autenticación ---

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): String
    @POST("api/auth/create-account")
    suspend fun createAccount(@Body request: RegisterRequest): Response<RegisterResponse>
    @PUT("api/auth/update/{id}")
    suspend fun updateProfile(
        @Path("id") userId: Int,
        @Body request: UpdateProfileRequest
    ): Response<User>
    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ResponseBody>
    // --- Vehículos ---
    @GET("api/cars")
    suspend fun getVehiculos(): List<Car>
    // --- Sucursales ---
    @GET("api/branches")
    suspend fun getSucursales(): List<Sucursal>

    // --- Reservas ---
    @POST("api/reservations")
    suspend fun createReservation(@Body request: ReservationRequest): CreateReservationResponse

    @GET("api/reservations/usuario/{id}")
    suspend fun getUserReservations(@Path("id") userId: Int): List<Reservation>

    @PUT("api/reservations/{id}")
    suspend fun updateReservation(
        @Path("id") reservationId: String,
        @Body request: ReservationRequest
    ): CreateReservationResponse

    // --- Usuarios ---

    @GET("api/users/{id}")
    suspend fun getUserProfile(@Path("id") userId: Int): User

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