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
import com.example.quadraandroidstudio.databinding.FragmentSelectionAutoBinding // NOTA: El nombre del binding cambiará
import com.example.quadraandroidstudio.model.Car
import com.google.android.material.chip.Chip

class SelectionAutoFragment : Fragment() {

    // 2. Patrón de View Binding para Fragments
    private var _binding: FragmentSelectionAutoBinding? = null
    private val binding get() = _binding!!

    private lateinit var carAdapter: CarAdapter

    // 3. El layout se infla en onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectionAutoBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 4. El resto de la lógica va en onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // La Toolbar ahora es solo una vista, no una ActionBar de la Activity
        // Por lo tanto, se eliminan setSupportActionBar y supportActionBar

        setupChips()
        setupRecyclerView()
        loadSampleCarData()

        // 5. La lógica del BottomNavigationView se ha ELIMINADO de aquí
    }

    private fun setupChips() {
        binding.chipGroupFilters.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = view?.findViewById<Chip>(checkedIds.first())
                chip?.let {
                    Log.d("SelectionAutoFragment", "Chip seleccionado: ${it.text}")
                    // Aquí implementarías la lógica de filtrado del RecyclerView
                }
            } else {
                Log.d("SelectionAutoFragment", "Ningún chip seleccionado")
            }
        }
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter { car ->
            // Lógica cuando se hace clic en el botón "Reservar" de un auto
            Log.d("SelectionAutoFragment", "Reservar clickeado para: ${car.name}")
            // Para navegar a la siguiente pantalla (por ejemplo, un fragmento de detalles del auto):
            // findNavController().navigate(R.id.action_selectionAutoFragment_to_carDetailFragment)
        }
        binding.recyclerViewAutos.apply {
            layoutManager = LinearLayoutManager(requireContext()) // 6. Usamos requireContext()
            adapter = carAdapter
        }
    }

    private fun loadSampleCarData() {
        val cars = listOf(
            Car(
                id = "1",
                name = "Lamborgnni Muanarior LP 700-4",
                brand = "Lamrfinghila",
                year = 2011,
                pricePerDay = 1200.0,
                imageUrl = "lamborghini_red" // Nombre del drawable
            ),
            Car(
                id = "2",
                name = "Lamborgnni Muanarior LP 700-4",
                brand = "Lamrfinghila",
                year = 2011,
                pricePerDay = 1200.0,
                imageUrl = "lamborghini_yellow" // Nombre del drawable
            )
        )
        carAdapter.submitList(cars)
    }

    // 7. Limpiar el binding para evitar fugas de memoria
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}