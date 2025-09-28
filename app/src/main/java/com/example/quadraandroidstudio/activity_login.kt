package com.example.quadraandroidstudio
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quadraandroidstudio.databinding.ActivityLoginBinding
import androidx.lifecycle.lifecycleScope
import com.example.quadraandroidstudio.data.LoginRequest
import com.example.quadraandroidstudio.network.RetrofitClient
import kotlinx.coroutines.launch

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
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val loginRequest = LoginRequest(email, password)

                // Usamos una coroutine para la llamada a la red
                lifecycleScope.launch {
                    try {
                        // Llamamos a la API
                        val response = RetrofitClient.instance.login(loginRequest)

                        // Si la llamada es exitosa, guardamos el token y navegamos
                        Toast.makeText(this@LoginActivity, "Login exitoso!", Toast.LENGTH_SHORT).show()
                        // Aquí guardarías el token: response.token

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } catch (e: Exception) {
                        // Si hay un error (ej. contraseña incorrecta, sin internet)
                        Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
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