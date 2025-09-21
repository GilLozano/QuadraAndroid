package com.example.quadraandroidstudio

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quadraandroidstudio.databinding.FragmentReserveLocationBinding // NOTA: El nombre del binding cambiará
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ReserveLocationFragment : Fragment() { // 1. Cambiado de AppCompatActivity a Fragment

    // 2. La forma correcta de manejar View Binding en un Fragment
    private var _binding: FragmentReserveLocationBinding? = null
    private val binding get() = _binding!!

    // 3. Inflar el layout se hace en onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReserveLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 4. El resto de la lógica va en onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        // 5. La lógica del BottomNavigationView se ha ELIMINADO
    }

    private fun setupClickListeners() {
        // Listener para el campo de Fecha de Inicio
        binding.tilDateStart.setOnClickListener {
            showDatePickerDialog(binding.etDateStart)
        }
        binding.etDateStart.setOnClickListener {
            showDatePickerDialog(binding.etDateStart)
        }

        // Listener para el campo de Fecha de Fin
        binding.tilDateEnd.setOnClickListener {
            showDatePickerDialog(binding.etDateEnd)
        }
        binding.etDateEnd.setOnClickListener {
            showDatePickerDialog(binding.etDateEnd)
        }

        // Listener para el botón Buscar Autos
        binding.btnSearchCars.setOnClickListener {
            val location = binding.etLocation.text.toString()
            val dateStart = binding.etDateStart.text.toString()
            val dateEnd = binding.etDateEnd.text.toString()

            if (location.isNotEmpty() && dateStart.isNotEmpty() && dateEnd.isNotEmpty()) {
                // 6. Usamos requireContext() en lugar de 'this' para el Context
                Toast.makeText(requireContext(), "Buscando autos en $location del $dateStart al $dateEnd", Toast.LENGTH_LONG).show()

                // La navegación ahora se haría con el Navigation Component
                // Ejemplo: findNavController().navigate(R.id.action_reserveLocationFragment_to_selectionAutoFragment)

            } else {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos de reserva", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), // 6. Usamos requireContext()
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                editText.setText(dateFormat.format(selectedDate.time))
            }, year, month, day)
        datePickerDialog.show()
    }

    // 7. Limpiar el binding para evitar fugas de memoria
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}