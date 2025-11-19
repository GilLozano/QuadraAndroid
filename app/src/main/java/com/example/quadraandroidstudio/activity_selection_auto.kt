package com.example.quadraandroidstudio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quadraandroidstudio.adapter.CarAdapter
import com.example.quadraandroidstudio.databinding.FragmentSelectionAutoBinding
import com.example.quadraandroidstudio.model.Car
import com.google.android.material.chip.Chip
import androidx.lifecycle.lifecycleScope
import com.example.quadraandroidstudio.network.RetrofitClient
import kotlinx.coroutines.launch
import java.lang.Exception
import android.widget.Toast

class SelectionAutoFragment : Fragment() {

    private var _binding: FragmentSelectionAutoBinding? = null
    private val binding get() = _binding!!

    private lateinit var carAdapter: CarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectionAutoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChips()
        setupRecyclerView()
        fetchCarsFromApi()

    }

    private fun setupChips() {
        binding.chipGroupFilters.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = view?.findViewById<Chip>(checkedIds.first())
                chip?.let {
                    Log.d("SelectionAutoFragment", "Chip seleccionado: ${it.text}")
                }
            } else {
                Log.d("SelectionAutoFragment", "Ningún chip seleccionado")
            }
        }
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter { car ->

            Log.d("SelectionAutoFragment", "Reservar clickeado para: ${car.marca} ${car.modelo}")

            findNavController().navigate(R.id.action_selectionAutoFragment_to_reserveFormFragment)
        }
        binding.recyclerViewAutos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = carAdapter
        }
    }

    private fun fetchCarsFromApi() {
        lifecycleScope.launch {
            try {
                val carList = RetrofitClient.instance.getVehiculos()
                carAdapter.submitList(carList) // Actualiza el RecyclerView con datos reales
                Log.d("SelectionAutoFragment", "Autos cargados: ${carList.size}")
            } catch (e: Exception) {
                Log.e("SelectionAutoFragment", "Error al obtener autos: ", e)
                Toast.makeText(requireContext(), "Error al cargar los autos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}