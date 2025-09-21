package com.example.quadraandroidstudio
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quadraandroidstudio.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    // Declara una variable para el View Binding
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla el layout usando View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar los listeners para los botones y textos
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Listener para el botón de Iniciar Sesión
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // Aquí iría tu lógica de validación y autenticación
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Simulación de un login exitoso
                Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()

                // 1. Crea un Intent para ir de LoginActivity a ReserveLocationActivity.
                val intent = Intent(this, MainActivity::class.java)

                // 2. Inicia la nueva actividad.
                startActivity(intent)

                // 3. (Opcional pero recomendado) Finaliza LoginActivity
                //    para que el usuario no pueda volver a la pantalla de login con el botón "atrás".
                finish()

                // Navegar a la siguiente pantalla, etc.
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener para "Crear cuenta"
        binding.tvCreateAccount.setOnClickListener {
            // Lógica para navegar a la pantalla de registro
            Toast.makeText(this, "Navegando a la pantalla de registro...", Toast.LENGTH_SHORT).show()
        }

        // Listener para "Olvidé la contraseña"
        binding.tvForgotPassword.setOnClickListener {
            // Lógica para navegar a la pantalla de recuperación de contraseña
            Toast.makeText(this, "Navegando a recuperación de contraseña...", Toast.LENGTH_SHORT).show()
        }
    }
}