package com.example.quadraandroidstudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quadraandroidstudio.R
import com.example.quadraandroidstudio.data.NewPasswordRequest
import com.example.quadraandroidstudio.databinding.FragmentResetPasswordBinding
import com.example.quadraandroidstudio.network.RetrofitClient
import kotlinx.coroutines.launch

class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private val args: ResetPasswordFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = args.token // Get token passed from ValidateTokenFragment

        binding.btnResetPassword.setOnClickListener {
            val password = binding.etNewPassword.text.toString()
            val passwordConfirmation = binding.etConfirmPassword.text.toString()

            if (password.isNotEmpty() && password == passwordConfirmation) {
                resetPassword(token, password)
            } else if (password != passwordConfirmation) {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Por favor, ingresa la nueva contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(token: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.setNewPasswordWithToken(token, NewPasswordRequest(password))
                Toast.makeText(requireContext(), response.mensaje, Toast.LENGTH_LONG).show()
                // Navigate back to Login screen after successful reset
                findNavController().navigate(R.id.action_resetPasswordFragment_to_loginActivity) // Adjust ID if Login is a fragment
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