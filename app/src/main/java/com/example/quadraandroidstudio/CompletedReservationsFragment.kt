package com.example.quadraandroidstudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quadraandroidstudio.databinding.FragmentCompletedReservationsBinding // Crearemos este binding
import com.example.quadraandroidstudio.adapter.ReservationAdapter // Crearemos este adapter
import com.example.quadraandroidstudio.model.Reservation // Crearemos este modelo
import com.example.quadraandroidstudio.model.Car // Usaremos el modelo Car

class CompletedReservationsFragment : Fragment() {

    private var _binding: FragmentCompletedReservationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var reservationAdapter: ReservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedReservationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadCompletedReservations()
    }

    private fun setupRecyclerView() {
        reservationAdapter = ReservationAdapter { reservation ->
            // Acción al hacer clic en un elemento de la lista (por ejemplo, "Ver Detalles")
            // Podrías navegar a una pantalla de detalles de reserva
        }
        binding.recyclerViewCompletedReservations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reservationAdapter
        }
    }

    private fun loadCompletedReservations() {
        // Datos de ejemplo para reservas completadas (igual que en tu imagen)
        val completedReservations = listOf(
            Reservation(
                id = "RES002",
                car = Car("2", "Lamborghini Aventador SV LP 770-4", "Lamborghini", 2022, 1800.0, "lamborghini_yellow"),
                startDate = "01/03/2024",
                endDate = "05/03/2024",
                status = "COMPLETADA",
                imageUrl = "lamborghini_yellow"
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