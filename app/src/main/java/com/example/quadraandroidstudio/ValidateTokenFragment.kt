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
import com.example.quadraandroidstudio.data.TokenRequest // Create this data class
import com.example.quadraandroidstudio.databinding.FragmentValidateTokenBinding // Create this layout
import com.example.quadraandroidstudio.network.RetrofitClient
import kotlinx.coroutines.launch

class ValidateTokenFragment : Fragment() {

    private var _binding: FragmentValidateTokenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentValidateTokenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnValidateToken.setOnClickListener {
            val token = binding.etToken.text.toString().trim()
            if (token.isNotEmpty()) {
                validateResetToken(token)
            } else {
                Toast.makeText(requireContext(), "Por favor, ingresa el token", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateResetToken(token: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.validateToken(TokenRequest(token))
                Toast.makeText(requireContext(), response.mensaje, Toast.LENGTH_SHORT).show()
                // Navigate to Reset Password screen, passing the token
                val action = ValidateTokenFragmentDirections.actionValidateTokenFragmentToResetPasswordFragment(token)
                findNavController().navigate(action)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}