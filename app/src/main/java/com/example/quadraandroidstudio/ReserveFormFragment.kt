package com.example.quadraandroidstudio

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.NavOptions
import com.example.quadraandroidstudio.R
import com.example.quadraandroidstudio.data.AlquilerData
import com.example.quadraandroidstudio.data.ReservationRequest
import com.example.quadraandroidstudio.databinding.FragmentReserveFormBinding
import com.example.quadraandroidstudio.model.Car
import com.example.quadraandroidstudio.model.Reservation
import com.example.quadraandroidstudio.model.Sucursal
import com.example.quadraandroidstudio.network.RetrofitClient
import com.example.quadraandroidstudio.utils.SharedPreferencesManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class ReserveFormFragment : Fragment() {

    private var _binding: FragmentReserveFormBinding? = null
    private val binding get() = _binding!!

    // Argumentos de navegación (Auto seleccionado O Reserva a editar)
    private val args: ReserveFormFragmentArgs by navArgs()

    // ViewModel compartido para datos del usuario
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Variables de control
    private lateinit var currentCar: Car
    private var reservationToEdit: Reservation? = null
    private var isEditMode: Boolean = false

    // Variables para el formulario
    private var startCalendar: Calendar? = null
    private var endCalendar: Calendar? = null
    private var calculatedTotalAmount: Double = 0.0
    private var sucursalesList: List<Sucursal> = emptyList()
    private var selectedSucursal: Sucursal? = null

    // Formato para parsear fechas que vienen del servidor (UTC)
    private val serverDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReserveFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Determinar si estamos en modo CREAR o EDITAR
        reservationToEdit = args.reservationToEdit
        val selectedCarArg = args.selectedCar

        if (reservationToEdit != null) {
            // --- MODO EDICIÓN ---
            isEditMode = true
            setupForEditMode(reservationToEdit!!)
        } else if (selectedCarArg != null) {
            // --- MODO CREACIÓN ---
            isEditMode = false
            currentCar = selectedCarArg
            setupForCreateMode()
        } else {
            // Error crítico: no se recibieron datos
            Toast.makeText(context, "Error al cargar datos para la reserva", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        // 2. Configuración común
        setupDatePickers()
        setupPaymentMethodPicker()
        // Cargar sucursales de la API (se seleccionará la correcta después si es edición)
        fetchSucursales()

        binding.btnReserveCar.setOnClickListener {
            submitForm()
        }
    }


    // --- FUNCIONES DE CONFIGURACIÓN INICIAL ---

    private fun setupForCreateMode() {
        binding.tvTitle.text = "Reservando: ${currentCar.marca} ${currentCar.modelo}"
        binding.btnReserveCar.text = "Confirmar Reserva"
        updateTotalAmountUI()

        // Autocompletar datos del usuario logueado
        sharedViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null && !isEditMode) {
                Log.d("ReserveFormFragment", "Autocompletando datos para: ${user.nombre}")
                binding.etName.setText(user.nombre)
                binding.etEmail.setText(user.email)
                binding.etPhone.setText(user.telefono ?: "")
            }
        }
    }

    private fun setupForEditMode(reservation: Reservation) {
        binding.tvTitle.text = "Editando Reserva"
        binding.btnReserveCar.text = "Guardar Cambios"

        // --- CORRECCIÓN AQUÍ: Usar el operador Elvis (?: "") ---
        // Si el dato viene nulo (reserva vieja), ponemos cadena vacía.
        binding.etName.setText(reservation.nombre ?: "")
        binding.etEmail.setText(reservation.email ?: "")
        binding.etPhone.setText(reservation.telefono ?: "")

        // Para el lugar de recogida, si es nulo, no seteamos nada.
        reservation.lugarRecogida?.let { lugar ->
            binding.etLocation.setText(lugar, false)
        }
        // ------------------------------------------------------

        // Reconstruir el objeto Car desde los detalles de la reserva
        val vDetails = reservation.vehiculo!!
        // Creamos un seguro "Dummy" (falso/vacío) para satisfacer al constructor de Car
        // ya que VehiculoDetails no trae info del seguro.
        val dummySeguro = com.example.quadraandroidstudio.model.Seguro(
            id = 0,
            tipo = "N/A",
            cobertura = "N/A",
            precio = 0.0,
            descripcion = ""
        )
        currentCar = Car(
            id = vDetails.id,
            marca = vDetails.marca,
            modelo = vDetails.modelo,
            anio = vDetails.anio,
            imagen = vDetails.imagen,
            // HACK TEMPORAL: Calculamos el precio diario basándonos en el total y los días.
            // Lo ideal sería que tu backend enviara el precioPorDia en VehiculoDetails.
            precioPorDia = reservation.alquiler.monto / calculateDays(reservation.fechaInicio, reservation.fechaFin),
            seguroId = 0,
            seguro = dummySeguro,
            // Datos no disponibles en el detalle de reserva, se dejan por defecto
            color = "", transmision = "", tipo = "", puertas = 0, asientos = 0, clima = false, estado = "", createdAt = null, updatedAt = null
        )

        // Pre-llenar datos personales
        // Como tu modelo de Reservation no guarda nombre/email/telefono, usamos los del usuario actual como fallback.
        sharedViewModel.currentUser.value?.let { user ->
            binding.etName.setText(user.nombre)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.telefono ?: "")
        }

        // Pre-llenar Fechas y Recalcular Total
        try {
            startCalendar = Calendar.getInstance().apply { time = serverDateFormat.parse(reservation.fechaInicio)!! }
            endCalendar = Calendar.getInstance().apply { time = serverDateFormat.parse(reservation.fechaFin)!! }
            updateDateInView(binding.etDateStart, startCalendar!!)
            updateDateInView(binding.etDateEnd, endCalendar!!)
            calculateTotal() // Recalcula el monto basado en el carro reconstruido
        } catch (e: Exception) {
            Log.e("EditMode", "Error parseando fechas para edición", e)
            Toast.makeText(context, "Error al cargar las fechas de la reserva", Toast.LENGTH_SHORT).show()
        }

        // Pre-llenar Método de Pago
        binding.etPaymentMethod.setText(reservation.alquiler.metodoPago)

        // NOTA SOBRE SUCURSAL: Como tu modelo 'Reservation' NO tiene el campo 'lugarRecogida',
        // no podemos pre-seleccionar la sucursal original. El usuario deberá elegirla de nuevo.
    }


    // --- LÓGICA DE DATOS Y UI ---

    private fun fetchSucursales() {
        lifecycleScope.launch {
            try {
                sucursalesList = RetrofitClient.instance.getSucursales()
                setupSucursalDropdown()

                // --- NUEVO: Si es modo edición, buscar y seleccionar la sucursal correcta ---
                if (isEditMode && reservationToEdit != null) {
                    // --- CORRECCIÓN AQUÍ: Usar let para asegurar que no sea nulo ---
                    reservationToEdit!!.lugarRecogida?.let { nombreLugarOriginal ->
                        selectedSucursal = sucursalesList.find { it.nombre == nombreLugarOriginal }

                        if (selectedSucursal != null) {
                            Log.d("EditMode", "Sucursal original encontrada y seleccionada internamente: ${selectedSucursal!!.nombre}")
                            // Opcional: aseguramos que el texto coincida
                            binding.etLocation.setText(selectedSucursal!!.nombre, false)
                        } else {
                            Log.w(
                                "EditMode",
                                "La sucursal original '$nombreLugarOriginal' ya no existe en la API."
                            )
                            // Aquí podrías mostrar un error o pedirle al usuario que seleccione una nueva.
                        }
                    }
                    // ---------------------------------------------------------------
                }
            } catch (e: Exception) {
                Log.e("ReserveFormFragment", "Error cargando sucursales", e)
                Toast.makeText(requireContext(), "Error al cargar lugares de recogida", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSucursalDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            sucursalesList
        )
        binding.etLocation.setAdapter(adapter)

        binding.etLocation.setOnItemClickListener { _, _, position, _ ->
            selectedSucursal = sucursalesList[position]
            Log.d("ReserveFormFragment", "Sucursal seleccionada: ${selectedSucursal?.nombre}")
        }
    }

    private fun calculateDays(startStr: String, endStr: String): Long {
        try {
            val start = serverDateFormat.parse(startStr)!!
            val end = serverDateFormat.parse(endStr)!!
            val diff = end.time - start.time
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            return if (days > 0) days else 1
        } catch (e: Exception) { return 1 }
    }

    private fun calculateTotal() {
        if (startCalendar != null && endCalendar != null && ::currentCar.isInitialized) {
            if (endCalendar!!.before(startCalendar)) {
                Toast.makeText(requireContext(), "La fecha de fin debe ser posterior a la de inicio", Toast.LENGTH_SHORT).show()
                // Resetear fecha fin inválida
                binding.etDateEnd.text = null
                endCalendar = null
                calculatedTotalAmount = 0.0
            } else {
                val diffInMillis = endCalendar!!.timeInMillis - startCalendar!!.timeInMillis
                val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                val billableDays = if (days > 0) days else 1
                calculatedTotalAmount = billableDays * currentCar.precioPorDia
            }
        } else {
            calculatedTotalAmount = 0.0
        }
        updateTotalAmountUI()
    }

    private fun updateTotalAmountUI() {
        binding.tvTotalAmount.text = "Total a pagar: $%.2f".format(calculatedTotalAmount)
    }


    // --- ENVÍO DEL FORMULARIO (CREAR O ACTUALIZAR) ---

    private fun submitForm() {
        // 1. Recolección de datos
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val paymentMethod = binding.etPaymentMethod.text.toString().trim()

        // 2. Validaciones
        if (selectedSucursal == null) {
            binding.tilLocation.error = "Selecciona un lugar de recogida"
            return
        } else {
            binding.tilLocation.error = null
        }

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() ||
            paymentMethod.isEmpty() || startCalendar == null || endCalendar == null) {
            Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (calculatedTotalAmount <= 0) {
            Toast.makeText(requireContext(), "Error en el cálculo del total. Revisa las fechas.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = SharedPreferencesManager.getUserId(requireContext())
        if (userId == -1) {
            Toast.makeText(requireContext(), "Sesión no válida. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
            return
        }

        // 3. Formatear fechas para la API (YYYY-MM-DD)
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val apiDateStart = apiDateFormat.format(startCalendar!!.time)
        val apiDateEnd = apiDateFormat.format(endCalendar!!.time)

        // 4. Construir el objeto de solicitud (común para crear y actualizar)
        val reservationRequest = ReservationRequest(
            usuarioId = userId,
            vehiculoId = currentCar.id,
            nombre = name,
            telefono = phone,
            email = email,
            fechaInicio = apiDateStart,
            fechaFin = apiDateEnd,
            lugarRecogida = selectedSucursal!!.nombre,
            alquiler = AlquilerData(
                monto = calculatedTotalAmount,
                metodoPago = paymentMethod
            )
        )

        // 5. Llamada a la API
        binding.btnReserveCar.isEnabled = false // Bloquear botón

        lifecycleScope.launch {
            try {
                val message = if (isEditMode) {
                    // --- ACTUALIZAR RESERVA EXISTENTE ---
                    // Nota: Asegúrate de que tu ApiService tenga la función updateReservation
                    RetrofitClient.instance.updateReservation(reservationToEdit!!.id, reservationRequest)
                    "Reserva actualizada correctamente"
                } else {
                    // --- CREAR NUEVA RESERVA ---
                    val response = RetrofitClient.instance.createReservation(reservationRequest)
                    response.message
                }

                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                // Volver al historial (usando popBackStack para que funcione desde cualquier origen)
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(findNavController().graph.id, true)
                    .setLaunchSingleTop(true)
                    .build()
                findNavController().navigate(
                    resId = R.id.historyFragment,
                    args = null,
                    navOptions = navOptions
                )

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("ReserveFormFragment", "Error details", e)
                binding.btnReserveCar.isEnabled = true // Desbloquear botón si falla
            }
        }
    }

    private fun setupDatePickers() {
        val dateSetListenerStart = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            startCalendar = Calendar.getInstance().apply { set(year, month, day) }
            updateDateInView(binding.etDateStart, startCalendar!!)
            // Al cambiar fecha de inicio, reseteamos fecha fin para evitar inconsistencias
            endCalendar = null
            binding.etDateEnd.text = null
            calculateTotal()
        }

        val dateSetListenerEnd = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            endCalendar = Calendar.getInstance().apply { set(year, month, day) }
            updateDateInView(binding.etDateEnd, endCalendar!!)
            calculateTotal()
        }

        binding.etDateStart.setOnClickListener {
            // Fecha mínima: hoy
            showDatePicker(startCalendar, dateSetListenerStart, minDate = System.currentTimeMillis())
        }

        binding.etDateEnd.setOnClickListener {
            // Fecha mínima para fin: 1 día después de la fecha de inicio seleccionada
            val minEndDate = startCalendar?.timeInMillis?.let { it + TimeUnit.DAYS.toMillis(1) } ?: System.currentTimeMillis()
            showDatePicker(endCalendar, dateSetListenerEnd, minDate = minEndDate)
        }
    }

    private fun showDatePicker(initialDate: Calendar?, listener: DatePickerDialog.OnDateSetListener, minDate: Long) {
        val cal = initialDate ?: Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            listener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = minDate
        }.show()
    }

    private fun updateDateInView(editText: android.widget.EditText, calendar: Calendar) {
        val myFormat = "dd/MM/yyyy" // Formato visual amigable
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        editText.setText(sdf.format(calendar.time))
    }

    private fun setupPaymentMethodPicker() {
        val paymentMethods = arrayOf("Tarjeta de Crédito", "Transferencia Bancaria", "Efectivo")
        binding.etPaymentMethod.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Seleccionar Método de Pago")
                .setItems(paymentMethods) { _, which ->
                    binding.etPaymentMethod.setText(paymentMethods[which])
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}