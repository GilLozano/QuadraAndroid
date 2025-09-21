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
        val pendingReservations = listOf(
            Reservation(
                id = "RES001",
                car = Car("1", "Lamborghini Huracan LP 610-4", "Lamborghini", 2023, 1500.0, "lamborghini_red"),
                startDate = "10/04/2024",
                endDate = "15/04/2024",
                status = "PENDIENTE",
                imageUrl = "lamborghini_red"
            )
            // Agrega más reservas pendientes si es necesario
        )
        reservationAdapter.submitList(pendingReservations)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}