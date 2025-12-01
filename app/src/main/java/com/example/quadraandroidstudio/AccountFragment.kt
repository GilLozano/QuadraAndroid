package com.example.quadraandroidstudio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quadraandroidstudio.LoginActivity
import com.example.quadraandroidstudio.R
import com.example.quadraandroidstudio.databinding.FragmentAccountBinding
import com.example.quadraandroidstudio.databinding.ItemAccountOptionBinding
import com.example.quadraandroidstudio.network.RetrofitClient
import com.example.quadraandroidstudio.utils.SharedPreferencesManager
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()
        setupOptionsUI()
        setupLogoutButton()

        // Configurar el contenido de cada opción individualmente
        setupOption(binding.optionAccountSettings, R.drawable.ic_account_settings, "Opciones de la Cuenta") {
            try {
                val action = AccountFragmentDirections.actionAccountFragmentToRegisterFragment(isEditMode = true)
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.e("AccountFragment", "Error al navegar a editar perfil", e)
                Toast.makeText(requireContext(), "Error al abrir opciones de cuenta", Toast.LENGTH_SHORT).show()
            }
        }
        setupOption(binding.optionSecurity, R.drawable.ic_security, "Contraseña y Seguridad") {
            try {
                findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
            } catch (e: Exception) {
                Log.e("AccountFragment", "Error de navegación", e)
            }
        }
        setupOption(binding.optionPaymentMethod, R.drawable.ic_payment_method, "Método de Pago") {
            Toast.makeText(requireContext(), "Navegar a Método de Pago", Toast.LENGTH_SHORT).show()
        }
        setupOption(binding.optionLegalPrivacy, R.drawable.ic_legal_privacy, "Legales y Privacidad") {
            Toast.makeText(requireContext(), "Navegar a Legales y Privacidad", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadUserProfile() {
        val userId = SharedPreferencesManager.getUserId(requireContext())

        if (userId == -1) {
            // Si no hay ID, no podemos cargar el perfil.
            binding.tvUserName.text = "Sesión no válida"
            binding.tvUserEmail.text = "-"
            return
        }

        // Mostrar "Cargando..." temporalmente
        binding.tvUserName.text = "Cargando..."
        binding.tvUserEmail.text = "..."

        lifecycleScope.launch {
            try {
                // Llamada a la API usando el ID guardado
                val user = RetrofitClient.instance.getUserProfile(userId)

                // Actualizar la UI con los datos recibidos
                binding.tvUserName.text = user.nombre
                binding.tvUserEmail.text = user.email

            } catch (e: Exception) {
                Log.e("AccountFragment", "Error cargando perfil", e)
                Toast.makeText(context, "Error al cargar perfil", Toast.LENGTH_SHORT).show()
                binding.tvUserName.text = "Error de carga"
                binding.tvUserEmail.text = "Reintenta más tarde"
            }
        }
    }

    private fun setupOptionsUI() {
        binding.optionLegalPrivacy.divider.visibility = View.GONE
    }

    private fun setupOption(
        optionBinding: ItemAccountOptionBinding,
        iconResId: Int,
        title: String,
        action: () -> Unit
    ) {
        // (Tu código existente) Configura icono, título y click listener
        optionBinding.ivOptionIcon.setImageResource(iconResId)
        optionBinding.tvOptionTitle.text = title
        optionBinding.root.setOnClickListener { action() }
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {

            performLogout()
        }
    }
    private fun performLogout() {
        // 1. Borrar datos de SharedPreferences
        SharedPreferencesManager.clearData(requireContext())

        // 2. Crear intent para ir al Login
        val intent = Intent(requireActivity(), LoginActivity::class.java)

        // 3. Limpiar la pila de actividades para que no se pueda volver atrás con el botón físico
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // 4. Iniciar la actividad de login
        startActivity(intent)

        // 5. Cerrar la actividad actual (MainActivity)
        requireActivity().finish()

        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}