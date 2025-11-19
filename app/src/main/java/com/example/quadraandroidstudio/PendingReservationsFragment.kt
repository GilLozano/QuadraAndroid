package com.example.quadraandroidstudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quadraandroidstudio.databinding.FragmentPendingReservationsBinding // Crearemos este binding
import com.example.quadraandroidstudio.adapter.ReservationAdapter // Crearemos este adapter
import com.example.quadraandroidstudio.model.Reservation // Crearemos este modelo
import com.example.quadraandroidstudio.model.Car // Usaremos el modelo Car
import com.example.quadraandroidstudio.model.Seguro

class PendingReservationsFragment : Fragment() {

    private var _binding: FragmentPendingReservationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var reservationAdapter: ReservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingReservationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadPendingReservations()
    }

    private fun setupRecyclerView() {
        reservationAdapter = ReservationAdapter { reservation ->
            // Acción al hacer clic en un elemento de la lista (por ejemplo, "Gestionar")
            // Podrías navegar a una pantalla de detalles de reserva
        }
        binding.recyclerViewPendingReservations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reservationAdapter
        }
    }

    private fun loadPendingReservations() {
        // Datos de ejemplo para reservas pendientes (igual que en tu imagen)
        val completedReservations = listOf(
            Reservation(
                id = "RES002",
                // ¡CAMBIO AQUÍ! Crea un objeto Car que coincida con el constructor completo
                car = Car(
                    id = 1, // Un ID de ejemplo
                    marca = "Lamborghini",
                    modelo = "Aventador SV LP 770-4",
                    color = "Amarillo",
                    anio = 2022,
                    transmision = "Automática",
                    tipo = "Deportivo",
                    puertas = 2,
                    asientos = 2,
                    clima = true,
                    precioPorDia = 1800.0,
                    seguroId = 1, // Un ID de seguro de ejemplo
                    imagen = "lamborghini_yellow", // El nombre del recurso drawable
                    estado = "Disponible", // Estado de ejemplo
                    seguro = Seguro( // Objeto Seguro de ejemplo
                        id = 1,
                        tipo = "Completo",
                        cobertura = "Daños a terceros, robo, todo riesgo",
                        precio = 200.0,
                        descripcion = "Seguro con amplia cobertura para vehículos deportivos."
                    ),
                    createdAt = null, // Opcional
                    updatedAt = null  // Opcional
                ),
                startDate = "01/03/2024",
                endDate = "05/03/2024",
                status = "COMPLETADA",
                imageUrl = "lamborghini_yellow" // Este campo en Reservation puede ser redundante si ya está en Car
            )
            // Agrega más reservas completadas si es necesario
        )
        reservationAdapter.submitList(completedReservations)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}