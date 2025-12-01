package com.example.quadraandroidstudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quadraandroidstudio.adapter.ReservationAdapter
import com.example.quadraandroidstudio.databinding.FragmentReservationListBinding

class PastReservationsFragment : Fragment() {

    private var _binding: FragmentReservationListBinding? = null
    private val binding get() = _binding!!
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
            Toast.makeText(context, "Historial: ${reservation.vehiculo?.modelo}", Toast.LENGTH_SHORT).show()
        }
        binding.rvReservations.adapter = adapter
    }

    private fun observeViewModel() {
        // Observamos la lista de PASADAS/HISTORIAL
        sharedViewModel.pastReservations.observe(viewLifecycleOwner) { reservations ->
            adapter.submitList(reservations)
            binding.tvEmptyView.visibility = if (reservations.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}