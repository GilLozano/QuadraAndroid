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


    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                        val token = RetrofitClient.instance.login(loginRequest)

                        Toast.makeText(this@LoginActivity, "Login exitoso!", Toast.LENGTH_SHORT).show()

                        // Navega a MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } catch (e: Exception) {
                        // Si hay un error (ej. contraseña incorrecta, error de red, error 500 del servidor)
                        // Muestra un mensaje más descriptivo si puedes interpretar el error de 'e'
                        Toast.makeText(this@LoginActivity, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_LONG).show()
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
            // Aquí podrías iniciar tu Activity/Fragment de registro
            // val intent = Intent(this, RegisterActivity::class.java)
            // startActivity(intent)
        }

        // Listener para "Olvidé la contraseña"
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Navegando a recuperación de contraseña...", Toast.LENGTH_SHORT).show()

            // 1. Crear Intent para ir a MainActivity
            val intent = Intent(this, MainActivity::class.java)

            // 2. Añadir un "extra" para decirle a MainActivity a dónde ir
            //    Usaremos el ID del fragmento definido en tu nav_graph.xml
            intent.putExtra("NAVIGATE_TO_DESTINATION", R.id.forgotPasswordFragment)

            // 3. Iniciar MainActivity
            startActivity(intent)

            // 4. (Opcional) Puedes cerrar LoginActivity si no quieres que el usuario vuelva aquí con "atrás"
            // finish()
        }
    }
}