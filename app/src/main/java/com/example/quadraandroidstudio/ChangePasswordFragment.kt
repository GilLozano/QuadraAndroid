package com.example.quadraandroidstudio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quadraandroidstudio.utils.SharedPreferencesManager
import com.example.quadraandroidstudio.data.ChangePasswordRequest
import com.example.quadraandroidstudio.databinding.FragmentChangePasswordBinding
import com.example.quadraandroidstudio.network.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnChangePassword.setOnClickListener {
            performChangePassword()
        }
    }

    private fun performChangePassword() {
        val currentPassword = binding.etCurrentPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmNewPassword = binding.etConfirmNewPassword.text.toString().trim()

        // 1. Validaciones locales
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            showError("Por favor, completa todos los campos.")
            return
        }

        if (newPassword.length < 8) {
            showError("La nueva contraseña debe tener al menos 8 caracteres.")
            return
        }

        if (newPassword != confirmNewPassword) {
            showError("Las nuevas contraseñas no coinciden.")
            binding.etConfirmNewPassword.text?.clear()
            return
        }

        val userId = SharedPreferencesManager.getUserId(requireContext())
        if (userId == -1) {
            showError("Error de sesión. Vuelve a iniciar sesión.")
            return
        }

        // --- NUEVO: Incluir el userId en la petición ---
        val request = ChangePasswordRequest(
            userId = userId,
            currentPassword = currentPassword,
            newPassword = newPassword
        )

        setLoading(true)

        // 3. Llamada a la API
        lifecycleScope.launch {
            try {
                // Asegúrate de que RetrofitClient use el interceptor de autenticación
                val response = RetrofitClient.instance.changePassword(request)

                if (response.isSuccessful) {
                    // Éxito: Mostramos mensaje y volvemos atrás
                    Toast.makeText(requireContext(), "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    // Error del servidor (ej. contraseña actual incorrecta)
                    handleApiError(response.errorBody()?.string())
                }

            } catch (e: Exception) {
                Log.e("ChangePassword", "Error de red", e)
                showError("No se pudo conectar con el servidor.")
            } finally {
                if (_binding != null) setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnChangePassword.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tilCurrentPassword.isEnabled = !isLoading
        binding.tilNewPassword.isEnabled = !isLoading
        binding.tilConfirmNewPassword.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun handleApiError(errorBody: String?) {
        val errorMessage = try {
            val jsonObject = JSONObject(errorBody ?: "")
            if (jsonObject.has("error")) {
                jsonObject.getString("error")
            } else if (jsonObject.has("errors")) {
                jsonObject.getJSONArray("errors").getJSONObject(0).getString("msg")
            } else {
                "Error al actualizar la contraseña."
            }
        } catch (e: Exception) {
            "Error al procesar la respuesta del servidor."
        }
        showError(errorMessage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}