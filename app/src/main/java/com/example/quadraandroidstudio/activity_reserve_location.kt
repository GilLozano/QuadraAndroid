package com.example.quadraandroidstudio

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quadraandroidstudio.databinding.ActivityReserveLocationBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReserveLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReserveLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReserveLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupBottomNavigationView()
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
                Toast.makeText(this, "Buscando autos en $location del $dateStart al $dateEnd", Toast.LENGTH_LONG).show()
                // Aquí iría la lógica para pasar a la pantalla de selección de auto
                // val intent = Intent(this, SelectCarActivity::class.java)
                // startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos de reserva", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog(editText: com.google.android.material.textfield.TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                editText.setText(dateFormat.format(selectedDate.time))
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home seleccionado", Toast.LENGTH_SHORT).show()
                    // Si ya estás en Home (ReserveLocationActivity), no hagas nada o refresca.
                    // Si Home es otra Activity, podrías iniciarla:
                    // startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_reservas -> {
                    Toast.makeText(this, "Reservas seleccionado", Toast.LENGTH_SHORT).show()
                    // val intent = Intent(this, ReservationsActivity::class.java)
                    // startActivity(intent)
                    true
                }
                R.id.nav_publish -> {
                    Toast.makeText(this, "Publicar seleccionado", Toast.LENGTH_SHORT).show()
                    // val intent = Intent(this, PublishActivity::class.java)
                    // startActivity(intent)
                    true
                }
                R.id.nav_account -> {
                    Toast.makeText(this, "Cuenta seleccionado", Toast.LENGTH_SHORT).show()
                    // val intent = Intent(this, AccountActivity::class.java)
                    // startActivity(intent)
                    true
                }
                else -> false
            }
        }
        // Establecer el item inicial como seleccionado si esta es la pantalla principal del "Home"
        binding.bottomNavigationView.selectedItemId = R.id.nav_home
    }
}