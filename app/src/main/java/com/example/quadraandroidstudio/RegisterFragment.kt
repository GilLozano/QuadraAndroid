package com.example.quadraandroidstudio

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.quadraandroidstudio.R
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quadraandroidstudio.MainActivity
import com.example.quadraandroidstudio.data.RegisterRequest
import com.example.quadraandroidstudio.data.UpdateProfileRequest
import com.example.quadraandroidstudio.data.RegisterResponse
import com.example.quadraandroidstudio.databinding.FragmentRegisterBinding
import com.example.quadraandroidstudio.network.RetrofitClient
import com.example.quadraandroidstudio.utils.SharedPreferencesManager
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!

    private var isEditMode = false
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            // Si hay argumentos, los extraemos usando la clase generada
            val safeArgs = RegisterFragmentArgs.fromBundle(requireArguments())
            isEditMode = safeArgs.isEditMode
        } else {
            // Si los argumentos son nulos (ej. cargado desde XML en RegisterActivity),
            // asumimos que NO es modo edición (es modo crear).
            isEditMode = false
        }

        if (isEditMode) {
            setupForEditMode()
        } else {
            setupForCreateMode()
        }

        binding.btnRegister.setOnClickListener {
            if (isEditMode) {
                performUpdate()
            } else {
                performRegistration()
            }
        }

        // El link al login solo tiene sentido en modo creación
        if (!isEditMode) {
            binding.tvGoToLogin.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_registerFragment)
            }
        } else {
            binding.tvGoToLogin.visibility = View.GONE
        }
    }

    private fun setupForCreateMode() {
        // Configuración por defecto del XML
        binding.tvTitle.text = "Crear Cuenta"
        binding.btnRegister.text = "Registrarse"
        // Asegurar que los campos de password sean visibles
        binding.tilPassword.visibility = View.VISIBLE
        binding.tilConfirmPassword.visibility = View.VISIBLE
    }

    private fun setupForEditMode() {
        binding.tvTitle.text = "Editar Perfil"
        binding.btnRegister.text = "Guardar Cambios"
        // Ocultar campos de contraseña (se cambian en otra pantalla)
        binding.ivAppLogo.visibility = View.GONE
        binding.tilPassword.visibility = View.GONE
        binding.ivLockIcon.visibility = View.GONE

        binding.tilConfirmPassword.visibility = View.GONE
        binding.ivConfirmLockIcon.visibility = View.GONE

        // Rellenar datos existentes desde el ViewModel
        sharedViewModel.currentUser.value?.let { user ->
            binding.etName.setText(user.nombre)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.telefono ?: "")
        }
    }

    private fun performUpdate() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val userId = SharedPreferencesManager.getUserId(requireContext())

        if (name.isEmpty() || email.isEmpty()) {
            showError("Nombre y Email son obligatorios.")
            return
        }

        if (userId == -1) {
            showError("Error de sesión. Vuelve a iniciar sesión.")
            return
        }

        val updateRequest = UpdateProfileRequest(name, email, phone)
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.updateProfile(userId, updateRequest)
                if (response.isSuccessful && response.body() != null) {
                    // 1. Actualizar el ViewModel con los nuevos datos
                    sharedViewModel.setCurrentUser(response.body()!!)
                    Toast.makeText(requireContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                    // 2. Volver atrás
                    findNavController().popBackStack()
                } else {
                    handleApiError(response.errorBody()?.string())
                }
            } catch (e: Exception) {
                handleNetworkError(e)
            } finally {
                if (_binding != null) setLoading(false)
            }
        }
    }



    private fun performRegistration() {
        // 1. Obtener los datos de los campos de texto
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // 2. Validaciones básicas en el frontend
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showError("Por favor, completa todos los campos.")
            return
        }

        if (password.length < 6) { // Ajusta según tus reglas
            showError("La contraseña debe tener al menos 6 caracteres.")
            return
        }
        if (password != confirmPassword) {
            showError("Las contraseñas no coinciden.")
            binding.etConfirmPassword.text?.clear()
            return
        }

        // 3. Crear el objeto para enviar al servidor
        // IMPORTANTE: Los nombres de los parámetros (nombre, email, etc.) deben coincidir
        // con los que definiste en tu data class RegisterRequest.
        val requestData = RegisterRequest(
            nombre = name,
            email = email,
            telefono = phone,
            password = password,
            passwordConfirmation = confirmPassword
        )

        // 4. Mostrar estado de carga
        setLoading(true)

        // 5. Llamada a la API en una corrutina
        lifecycleScope.launch {
            try {
                Log.d("RegisterFragment", "Enviando datos de registro: $email")
                val response = RetrofitClient.instance.createAccount(requestData)

                if (response.isSuccessful) {
                    // --- ÉXITO: EL BACKEND NOS DEVOLVIÓ TOKEN Y ID ---
                    val registerResponse = response.body()

                    if (registerResponse != null && registerResponse.token.isNotEmpty()) {
                        Log.d("RegisterFragment", "Registro exitoso. ID: ${registerResponse.userId}")

                        // A) Guardar credenciales (Token y ID) en SharedPreferences
                        // Usamos requireContext() porque estamos en un fragmento
                        SharedPreferencesManager.saveAuthToken(requireContext(), registerResponse.token)
                        SharedPreferencesManager.saveUserId(requireContext(), registerResponse.userId)

                        Toast.makeText(requireContext(), "¡Bienvenido, $name!", Toast.LENGTH_SHORT).show()

                        // B) Navegar directamente a la pantalla principal (MainActivity)
                        // Esto salta el login y evita volver atrás al registro.
                        goToMainActivity()

                    } else {
                        showError("Error: Respuesta del servidor incompleta.")
                    }

                } else {
                    // --- ERROR DEL SERVIDOR (ej. 409 Email duplicado, 400 datos inválidos) ---
                    // Intentamos leer el mensaje de error que envía el backend en formato JSON
                    val errorBodyStr = response.errorBody()?.string()
                    Log.e("RegisterFragment_DEBUG", "Cuerpo del error crudo: $errorBodyStr")
                    val errorMessage = try {
                        // Asume que tu backend devuelve {"error": "El correo ya existe"}
                        val jsonObject = JSONObject(errorBodyStr!!)
                        jsonObject.getString("error")
                    } catch (_: Exception) {
                        "Error en el registro. Inténtalo de nuevo."
                    }
                    Log.e("RegisterFragment", "Error API: $errorMessage")
                    showError(errorMessage)
                }

            } catch (e: Exception) {
                // --- ERROR DE RED O EXCEPCIÓN DE LA APP ---
                Log.e("RegisterFragment", "Excepción de red", e)
                showError("No se pudo conectar con el servidor. Revisa tu conexión.")
            } finally {
                // 6. Ocultar estado de carga (siempre se ejecuta)
                // Verificamos que el binding siga vivo para evitar crashes si el usuario salió rápido
                if (_binding != null) {
                    setLoading(false)
                }
            }
        }
    }

    private fun goToMainActivity() {
        // Creamos un Intent para ir a la Activity principal
        val intent = Intent(requireActivity(), MainActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        // Cerramos la actividad actual (la que contiene este fragmento, ej. AuthActivity)
        requireActivity().finish()
    }

    // Funciones auxiliares para manejar la UI

    @SuppressLint("SetTextI18n")
    private fun setLoading(isLoading: Boolean) {
        binding.btnRegister.isEnabled = !isLoading
        binding.etName.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etPhone.isEnabled = !isLoading

        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnRegister.text = "" // Ocultar texto del botón mientras carga
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnRegister.text = "Registrarse" // Restaurar texto
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun handleApiError(errorBody: String?) {
        val errorMessage = try {
            // Intenta leer formato {"error": "msg"} o {"errors": [...]}
            val jsonObject = JSONObject(errorBody ?: "")
            if (jsonObject.has("error")) {
                jsonObject.getString("error")
            } else if (jsonObject.has("errors")) {
                jsonObject.getJSONArray("errors").getJSONObject(0).getString("msg")
            } else {
                "Error en la solicitud."
            }
        } catch (_: Exception) {
            "Error al procesar la respuesta del servidor."
        }
        showError(errorMessage)
    }

    private fun handleNetworkError(e: Exception) {
        Log.e("RegisterFragment", "Error de red", e)
        showError("No se pudo conectar con el servidor.")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Evitar fugas de memoria liberando el binding
        _binding = null
    }
}