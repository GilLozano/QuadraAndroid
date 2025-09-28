package com.example.quadraandroidstudio

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quadraandroidstudio.databinding.FragmentReserveFormBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReserveFormFragment : Fragment() {

    private var _binding: FragmentReserveFormBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReserveFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Campo de Lugar (etLocation)
        binding.etLocation.setOnClickListener {
            // Aquí puedes implementar la lógica para seleccionar un lugar.
            // Podría ser un AlertDialog con una lista, o navegar a un mapa.
            Toast.makeText(context, "Seleccionar Lugar", Toast.LENGTH_SHORT).show()
            // Ejemplo: showLocationSelectionDialog()
        }

        // Campo de Fecha de Inicio (etDateStart)
        binding.etDateStart.setOnClickListener {
            showDatePicker(binding.etDateStart)
        }

        // Campo de Método de Pago (etPaymentMethod)
        binding.etPaymentMethod.setOnClickListener {
            // Aquí puedes implementar la lógica para seleccionar un método de pago.
            // Podría ser un AlertDialog con opciones, o navegar a una pantalla de gestión de pagos.
            Toast.makeText(context, "Seleccionar Método de Pago", Toast.LENGTH_SHORT).show()
            // Ejemplo: showPaymentMethodSelectionDialog()
        }

        // Botón Reservar Auto
        binding.btnReserveCar.setOnClickListener {
            processReservation()
        }
    }

    // Función para mostrar el selector de fecha (DatePickerDialog)
    private fun showDatePicker(targetEditText: com.google.android.material.textfield.TextInputEditText) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView(targetEditText)
        }

        context?.let {
            DatePickerDialog(
                it,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // Función para actualizar el texto del campo de fecha con la fecha seleccionada
    private fun updateDateInView(targetEditText: com.google.android.material.textfield.TextInputEditText) {
        val myFormat = "dd/MM/yyyy" // Formato de fecha deseado
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        targetEditText.setText(sdf.format(calendar.time))
    }

    // Lógica para procesar la reserva
    private fun processReservation() {
        val location = binding.etLocation.text.toString()
        val dateStart = binding.etDateStart.text.toString()
        val paymentMethod = binding.etPaymentMethod.text.toString()

        if (location.isBlank() || dateStart.isBlank() || paymentMethod.isBlank()) {
            Toast.makeText(context, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
        } else {
            // Aquí iría la lógica real para enviar los datos de la reserva
            // a un servidor, guardar en base de datos local, etc.
            Toast.makeText(context, "Reserva para '$location' desde '$dateStart' con '$paymentMethod' procesada.", Toast.LENGTH_LONG).show()

            // Ejemplo: Navegar a una pantalla de confirmación o de éxito
            // findNavController().navigate(R.id.action_reserveFormFragment_to_confirmationFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}