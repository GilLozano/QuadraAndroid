package com.example.quadraandroidstudio

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quadraandroidstudio.R
import com.example.quadraandroidstudio.data.CreateReservationResponse
import com.example.quadraandroidstudio.data.ErrorResponse
import com.example.quadraandroidstudio.data.ReservationRequest
import com.example.quadraandroidstudio.databinding.FragmentReserveFormBinding
import com.example.quadraandroidstudio.model.Car
import com.example.quadraandroidstudio.network.RetrofitClient
import com.example.quadraandroidstudio.utils.SharedPreferencesManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReserveFormFragment : Fragment() {

    private var _binding: FragmentReserveFormBinding? = null
    private val binding get() = _binding!!

    // Datos que deberías recibir del fragmento anterior o SharedPreferences
    private var selectedCar: Car? = null
    private var selectedStartDate: Calendar? = null
    private var selectedEndDate: Calendar? = null
    private var selectedLocation: String? = null // Lugar de recogida

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReserveFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Recuperar argumentos (ejemplo) ---
        // Si pasas el coche seleccionado, las fechas y el lugar como argumentos
        arguments?.let {
            // Aquí necesitarías claves para tus argumentos, ej. "selected_car", "start_date", "end_date", "location"
            // selectedCar = it.getSerializable("selected_car") as? Car
            // val startDateMillis = it.getLong("start_date", 0L)
            // if (startDateMillis != 0L) selectedStartDate = Calendar.getInstance().apply { timeInMillis = startDateMillis }
            // val endDateMillis = it.getLong("end_date", 0L)
            // if (endDateMillis != 0L) selectedEndDate = Calendar.getInstance().apply { timeInMillis = endDateMillis }
            // selectedLocation = it.getString("location")

            // Cargar datos en los EditTexts si ya los tienes
            // binding.etLocation.setText(selectedLocation)
            // selectedStartDate?.let { cal ->
            //     binding.etDateStart.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time))
            // }
            // selectedEndDate?.let { cal ->
            //     binding.etDateEnd.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time))
            // }
        }

        setupDatePickers()
        setupPaymentMethodPicker()
        binding.btnReserveCar.setOnClickListener {
            attemptCreateReservation()
        }

        // Opcional: Llenar etLocation y etDateStart/End si vienen de un fragmento anterior
        // Por ejemplo, si vienes de ReserveLocationFragment
        // (Necesitarías pasar argumentos de navegación del fragmento anterior)
    }

    private fun setupDatePickers() {
        // Ejemplo de un solo DatePicker para "Fecha de Inicio"
        // Puedes duplicar esto para "Fecha de Fin" si es necesario
        binding.etDateStart.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                selectedStartDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedStartDate!!.time)
                binding.etDateStart.setText(formattedDate)
            }, year, month, day)
            datePickerDialog.show()
        }
        // Agrega un datePicker para fecha de fin si no lo tienes
        // binding.etDateEnd.setOnClickListener { ... }
    }

    private fun setupPaymentMethodPicker() {
        val paymentMethods = arrayOf("Tarjeta de Crédito", "Transferencia Bancaria", "PayPal")

        binding.etPaymentMethod.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Seleccionar Método de Pago")
                .setItems(paymentMethods) { dialog, which ->

                    binding.etPaymentMethod.setText(paymentMethods[which])
                }
                .show()
        }
    }

    private fun attemptCreateReservation() {
        val location = binding.etLocation.text.toString().trim()
        val dateStart = binding.etDateStart.text.toString().trim()
        val dateEnd = "" // Asumiendo que aún no tienes un campo para fecha de fin en este layout
        val paymentMethod = binding.etPaymentMethod.text.toString().trim()

        // Validación de campos
        if (location.isEmpty() || dateStart.isEmpty() || paymentMethod.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Aquí necesitas el ID del auto seleccionado (selectedCar.id) y el ID del usuario
        val carId = selectedCar?.id // Obtener el ID del auto
            ?: run { // Si selectedCar es null, muestra un error y sal
                Toast.makeText(requireContext(), "Error: No se seleccionó ningún auto.", Toast.LENGTH_LONG).show()
                return
            }

        val userId = SharedPreferencesManager.getUserId(requireContext()) // Obtener ID del usuario
        if (userId == -1) { // -1 sería un valor por defecto si no se encuentra
            Toast.makeText(requireContext(), "Error: Usuario no autenticado.", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.loginActivity) // Redirigir al login
            return
        }

        // Formatear fechas para la API (si tu API espera "YYYY-MM-DD")
        // Necesitarías un EditText para fecha de fin también
        val apiDateStart = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedStartDate?.time ?: Date())
        val apiDateEnd = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedEndDate?.time ?: Date())


        val reservationRequest = ReservationRequest(
            vehiculoId = carId,
            usuarioId = userId,
            fechaInicio = apiDateStart,
            fechaFin = apiDateEnd, // Asegúrate de tener esta fecha o un valor por defecto
            lugarRecogida = location,
            metodoPago = paymentMethod,
            estado = "PENDIENTE" // Estado inicial
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.createReservation(reservationRequest)
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()

                // Navegar a una pantalla de confirmación o al historial de reservas
                findNavController().navigate(R.id.historyFragment) // Asumiendo que nav_history es el ID de tu fragmento de historial

            } catch (e: Exception) {
                // Manejo de errores más detallado
                val errorMessage = when (e) {
                    is retrofit2.HttpException -> {
                        val errorBody = e.response()?.errorBody()?.string()
                        // Intentar parsear el errorBody como JSON si tu API devuelve JSON en errores
                        try {
                            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                            errorResponse.error
                        } catch (jsonEx: Exception) {
                            errorBody ?: "Error HTTP desconocido"
                        }
                    }
                    else -> e.message ?: "Error de red o desconocido"
                }
                Toast.makeText(requireContext(), "Error al reservar: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}