package com.example.quadraandroidstudio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quadraandroidstudio.databinding.ActivityLoginBinding
import androidx.lifecycle.lifecycleScope
import com.example.quadraandroidstudio.data.LoginRequest
import com.example.quadraandroidstudio.network.RetrofitClient
import com.auth0.android.jwt.JWT
import com.example.quadraandroidstudio.utils.SharedPreferencesManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isUserLoggedIn()) {
            // Si ya está logueado, saltamos directo a MainActivity
            navigateToMain()
            // Importante: Usamos 'return' para que no siga ejecutando el resto del onCreate
            // y no intente mostrar el formulario de login un milisegundo antes de cambiar.
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()
    }

    private fun isUserLoggedIn(): Boolean {
        val userId = SharedPreferencesManager.getUserId(this)
        // Si es diferente de -1, significa que hay un ID guardado
        return userId != -1
    }

    // Función para ir a la pantalla principal y cerrar la de login
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        // 'finish()' es crucial. Cierra LoginActivity para que si el usuario
        // presiona "Atrás" en MainActivity, se salga de la app en lugar de volver al login.
        finish()
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
                        // 1. Llamamos a la API y recibimos el token (String)
                        val token = RetrofitClient.instance.login(loginRequest)


                        // 2. Decodificamos el token usando la librería auth0
                        val jwt = JWT(token)

                        // 3. Extraemos el 'id' del payload del token.
                        //    Usamos .asInt() porque tu ID de usuario es un número entero.
                        val userId = jwt.getClaim("id").asInt()

                        if (userId != null) {
                            // 4. Guardamos el Token y el ID en el teléfono para usarlos luego
                            SharedPreferencesManager.saveAuthToken(this@LoginActivity, token)
                            SharedPreferencesManager.saveUserId(this@LoginActivity, userId)

                            Log.d("LoginActivity", "Login exitoso. ID guardado: $userId")
                            Toast.makeText(this@LoginActivity, "Login exitoso!", Toast.LENGTH_SHORT).show()

                            // 5. Navegamos a MainActivity solo si todo salió bien
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Cerramos LoginActivity
                        } else {
                            // Esto pasa si el token que envió el servidor no tiene el campo "id"
                            Toast.makeText(this@LoginActivity, "Error de autenticación: Token inválido.", Toast.LENGTH_LONG).show()
                        }



                    } catch (e: Exception) {
                        // Si hay un error (ej. contraseña incorrecta, error de red, error 500 del servidor)
                        Log.e("LoginActivity", "Error en login", e)
                        Toast.makeText(this@LoginActivity, "Error al iniciar sesión. Verifica tus datos.", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener para "Crear cuenta"
        binding.tvCreateAccount.setOnClickListener {
            // Creamos el Intent para ir a la RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Listener para "Olvidé la contraseña"
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Navegando a forgot password...", Toast.LENGTH_SHORT).show()
        }
    }
}