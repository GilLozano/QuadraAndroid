package com.example.quadraandroidstudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quadraandroidstudio.R
import com.example.quadraandroidstudio.data.EmailRequest
import com.example.quadraandroidstudio.databinding.FragmentForgotPasswordBinding
import com.example.quadraandroidstudio.network.RetrofitClient
import com.example.quadraandroidstudio.data.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRequestReset.setOnClickListener {
            val email = binding.etEmailForgot.text.toString().trim()
            if (email.isNotEmpty()) {
                requestPasswordReset(email)
            } else {
                Toast.makeText(requireContext(), "Por favor, ingresa tu correo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPasswordReset(email: String) {
        lifecycleScope.launch {
            try {
                // response aquí será un String (el mensaje de éxito o un JSON de error como String)
                val rawResponse = RetrofitClient.instance.forgotPassword(EmailRequest(email))

                // Intentar parsear el String como JSON si es un error (ej. 404)
                if (rawResponse.startsWith("{") && rawResponse.endsWith("}")) {
                    // Si es un JSON, intentar obtener el error
                    val errorResponse = Gson().fromJson(rawResponse, ErrorResponse::class.java) // Necesitas una clase ErrorResponse
                    Toast.makeText(requireContext(), "Error: ${errorResponse.error}", Toast.LENGTH_LONG).show()
                } else {
                    // Si es texto plano, es el mensaje de éxito
                    Toast.makeText(requireContext(), rawResponse, Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_validateTokenFragment)
                }
            } catch (e: HttpException) { // Captura errores HTTP como 404 o 500
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    // Intenta parsear el errorBody como JSON (tu API devuelve JSON en 404)
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.error
                } catch (jsonException: Exception) {
                    // Si no es JSON, usa el body crudo
                    errorBody ?: "Error desconocido"
                }
                Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_LONG).show()
            } catch (e: Exception) { // Captura otros errores de red/parseo
                Toast.makeText(requireContext(), "Error general: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}