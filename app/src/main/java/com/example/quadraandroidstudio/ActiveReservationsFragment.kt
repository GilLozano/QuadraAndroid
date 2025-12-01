package com.example.quadraandroidstudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.quadraandroidstudio.adapter.ReservationAdapter
import com.example.quadraandroidstudio.databinding.FragmentReservationListBinding
import com.example.quadraandroidstudio.HistoryFragmentDirections

class ActiveReservationsFragment : Fragment() {

    private var _binding: FragmentReservationListBinding? = null
    private val binding get() = _binding!!
    // Usamos activityViewModels para compartir datos con la activity principal
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var adapter: ReservationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReservationListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ReservationAdapter { reservation ->
            // 1. Verificar si el estado permite edición.
            // Usa el string EXACTO que devuelve tu backend para "pendiente".
            if (reservation.estado == "Vehiculo En Proceso de Entrega") {

                // 2. Crear la acción de navegación pasando la reserva
                // Nota: selectedCar lo pasamos como null porque la reserva ya trae el auto
                val action = HistoryFragmentDirections
                    .actionHistoryFragmentToReserveFormFragment(selectedCar = null, reservationToEdit = reservation)

                // 3. Navegar
                findNavController().navigate(action)

            } else {
                Toast.makeText(context, "Esta reserva ya no se puede editar", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvReservations.adapter = adapter

    }

    private fun observeViewModel() {
        // Observamos la lista de ACTIVAS
        sharedViewModel.activeReservations.observe(viewLifecycleOwner) { reservations ->
            adapter.submitList(reservations)
            binding.tvEmptyView.visibility = if (reservations.isEmpty()) View.VISIBLE else View.GONE
        }
        // Observamos el estado de carga
        sharedViewModel.isLoadingReservations.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}