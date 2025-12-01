package com.example.quadraandroidstudio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quadraandroidstudio.R
import com.example.quadraandroidstudio.adapter.CarAdapter
import com.example.quadraandroidstudio.databinding.FragmentSelectionAutoBinding
import com.example.quadraandroidstudio.model.Car
import com.example.quadraandroidstudio.network.RetrofitClient
import kotlinx.coroutines.launch

class SelectionAutoFragment : Fragment() {

    private var _binding: FragmentSelectionAutoBinding? = null
    private val binding get() = _binding!!
    private lateinit var carAdapter: CarAdapter

    // Lista completa de autos descargados de la API (sin filtrar)
    private var allCarsList: List<Car> = emptyList()

    // Listas para almacenar los filtros seleccionados (SE ELIMINÓ selectedColors)
    private val selectedBrands = mutableSetOf<String>()
    private val selectedYears = mutableSetOf<Int>()
    private val selectedTypes = mutableSetOf<String>()
    private val selectedTransmissions = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectionAutoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilterAccordionUI()
        setupFilterCheckboxes()
        fetchCarsFromApi()
    }

    // --- 1. Configuración de la UI (Acordeón) ---
    private fun setupFilterAccordionUI() {
        // Helper para alternar visibilidad
        fun toggleVisibility(container: View) {
            container.visibility = if (container.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        binding.headerMarca.setOnClickListener { toggleVisibility(binding.containerMarca) }
        binding.headerAnio.setOnClickListener { toggleVisibility(binding.containerAnio) }
        // SE ELIMINÓ headerColor
        binding.headerTipo.setOnClickListener { toggleVisibility(binding.containerTipo) }
        binding.headerTransmision.setOnClickListener { toggleVisibility(binding.containerTransmision) }
    }

    // --- 2. Configuración de Listeners de Checkboxes ---
    private fun setupFilterCheckboxes() {
        // MARCAS
        binding.cbToyota.setOnCheckedChangeListener { _, isChecked -> updateFilter(selectedBrands, "Toyota", isChecked) }
        binding.cbHyundai.setOnCheckedChangeListener { _, isChecked -> updateFilter(selectedBrands, "Hyundai", isChecked) }
        binding.cbKia.setOnCheckedChangeListener { _, isChecked -> updateFilter(selectedBrands, "Kia", isChecked) }

        // AÑOS
        binding.cb2022.setOnCheckedChangeListener { _, isChecked -> updateFilter(selectedYears, 2022, isChecked) }
        binding.cb2021.setOnCheckedChangeListener { _, isChecked -> updateFilter(selectedYears, 2021, isChecked) }
        binding.cb2020.setOnCheckedChangeListener { _, isChecked -> updateFilter(selectedYears, 2020, isChecked) }

        // SE ELIMINARON LOS LISTENERS DE COLOR (cbBlanco, cbGris, cbRojo)

        // TIPOS
        binding.cbSedan.setOnCheckedChangeListener { _, isChecked -> updateFilter(selectedTypes, "Sedán", isChecked) }
        binding.cbSUV.setOnCheckedChangeListener { _, isChecked -> updateFilter(selectedTypes, "SUV", isChecked) }
        binding.cbDeportivo.setOnCheckedChangeListener { _, isChecked -> updateFilter(selectedTypes, "Deportivo", isChecked) }

        // TRANSMISIÓN
        binding.cbAutomatica.setOnCheckedChangeListener { _, isChecked -> updateFilter(selectedTransmissions, "Automática", isChecked) }
        binding.cbManual.setOnCheckedChangeListener { _, isChecked -> updateFilter(selectedTransmissions, "Manual", isChecked) }
    }

    // Helper genérico para actualizar los sets de filtros
    private fun <T> updateFilter(filterSet: MutableSet<T>, value: T, isChecked: Boolean) {
        if (isChecked) {
            filterSet.add(value)
        } else {
            filterSet.remove(value)
        }
        applyFilters()
    }


    // --- 3. Lógica de Filtrado Multicriterio ---
    private fun applyFilters() {
        var filteredList = allCarsList

        // Filtro MARCA
        if (selectedBrands.isNotEmpty()) {
            filteredList = filteredList.filter { car ->
                selectedBrands.any { it.equals(car.marca, ignoreCase = true) }
            }
        }

        // Filtro AÑO
        if (selectedYears.isNotEmpty()) {
            filteredList = filteredList.filter { car ->
                selectedYears.contains(car.anio)
            }
        }

        // SE ELIMINÓ EL BLOQUE DE FILTRO POR COLOR

        // Filtro TIPO
        if (selectedTypes.isNotEmpty()) {
            filteredList = filteredList.filter { car ->
                selectedTypes.any { it.equals(car.tipo, ignoreCase = true) }
            }
        }

        // Filtro TRANSMISIÓN
        if (selectedTransmissions.isNotEmpty()) {
            filteredList = filteredList.filter { car ->
                selectedTransmissions.any { it.equals(car.transmision, ignoreCase = true) }
            }
        }

        carAdapter.submitList(filteredList)
        Log.d("SelectionAutoFragment", "Filtros aplicados. Resultados: ${filteredList.size}")
    }


    private fun setupRecyclerView() {
        // EL ERROR ESTÁ AQUÍ DENTRO
        carAdapter = CarAdapter { car ->
            Log.d("SelectionAutoFragment", "Reservar clickeado para: ${car.marca} ${car.modelo}")

            // --- CÓDIGO INCORRECTO (BORRA ESTO SI LO TIENES ASÍ) ---
            // findNavController().navigate(R.id.action_selectionAutoFragment_to_reserveFormFragment)
            // -------------------------------------------------------


            // --- CÓDIGO CORRECTO (PON ESTO EN SU LUGAR) ---
            // 1. Usamos la clase "Directions" generada automáticamente.
            // 2. Esta función TE OBLIGA a pasar el 'selectedCar'.
            val action = SelectionAutoFragmentDirections
                .actionSelectionAutoFragmentToReserveFormFragment(selectedCar = car, reservationToEdit = null)

            // 3. Navegamos usando la acción segura que lleva el auto dentro.
            findNavController().navigate(action)
            // ------------------------------------------------
        }

        binding.recyclerViewAutos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = carAdapter
        }
    }

    private fun fetchCarsFromApi() {
        lifecycleScope.launch {
            try {
                allCarsList = RetrofitClient.instance.getVehiculos()
                Log.d("SelectionAutoFragment", "Autos cargados de API: ${allCarsList.size}")
                applyFilters() // Aplicar filtros iniciales (mostrará todos)
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