package com.example.quadraandroidstudio

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quadraandroidstudio.model.User
import com.example.quadraandroidstudio.model.Reservation
import com.example.quadraandroidstudio.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

// Este ViewModel servirá para compartir datos entre fragmentos y la actividad principal
class SharedViewModel : ViewModel() {

    // LiveData que contendrá el usuario actual. Los fragmentos observarán esto.
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }
    // Función para llamar a la API y cargar los datos del usuario
    // Esta función la llamará la MainActivity al iniciar.
    fun fetchUserProfile(userId: Int) {
        viewModelScope.launch {
            try {
                Log.d("SharedViewModel", "Cargando perfil para usuario ID: $userId...")
                val user = RetrofitClient.instance.getUserProfile(userId)
                _currentUser.value = user // ¡Aquí se guardan los datos!
                Log.d("SharedViewModel", "Perfil cargado exitosamente: ${user.nombre}")
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error al cargar perfil", e)
                _currentUser.value = null // Si falla, aseguramos que esté nulo
            }
        }
    }

    // Función para limpiar los datos al cerrar sesión
    fun clearUserData() {
        _currentUser.value = null
        _activeReservations.value = emptyList()
        _pastReservations.value = emptyList()
    }

    private val _activeReservations = MutableLiveData<List<Reservation>>()
    val activeReservations: LiveData<List<Reservation>> = _activeReservations

    private val _pastReservations = MutableLiveData<List<Reservation>>()
    val pastReservations: LiveData<List<Reservation>> = _pastReservations

    private val _isLoadingReservations = MutableLiveData<Boolean>(false)
    val isLoadingReservations: LiveData<Boolean> = _isLoadingReservations

    fun fetchUserReservations(userId: Int) {
        _isLoadingReservations.value = true
        viewModelScope.launch {
            try {
                val allReservations = RetrofitClient.instance.getUserReservations(userId)
                val (active, past) = filterReservations(allReservations)
                _activeReservations.value = active
                _pastReservations.value = past
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error cargando reservas", e)
                // Podrías manejar un LiveData de error aquí si quisieras
            } finally {
                _isLoadingReservations.value = false
            }
        }
    }

    private fun filterReservations(list: List<Reservation>): Pair<List<Reservation>, List<Reservation>> {
        val activeList = mutableListOf<Reservation>()
        val pastList = mutableListOf<Reservation>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        for (res in list) {
            try {
                val endDate = dateFormat.parse(res.fechaFin)
                // CORRECCIÓN DE KOTLIN: Usamos >= en lugar de .after || .equals
                if (endDate != null && endDate >= today) {
                    activeList.add(res)
                } else {
                    pastList.add(res)
                }
            } catch (_: Exception) {
                pastList.add(res)
            }
        }
        activeList.sortBy { it.fechaInicio }
        pastList.sortByDescending { it.fechaFin }
        return Pair(activeList, pastList)
    }
}
