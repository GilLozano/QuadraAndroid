package com.example.quadraandroidstudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quadraandroidstudio.R
import com.example.quadraandroidstudio.databinding.FragmentAccountBinding
import com.example.quadraandroidstudio.databinding.ItemAccountOptionBinding // Para el binding de las opciones incluidas

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

        setupOptions()
        setupLogoutButton()

        // Configurar el contenido de cada opción individualmente
        setupOption(binding.optionAccountSettings, R.drawable.ic_account_settings, "Opciones de la Cuenta") {
            Toast.makeText(requireContext(), "Navegar a Opciones de la Cuenta", Toast.LENGTH_SHORT).show()
            // Aquí puedes añadir la navegación a otra pantalla de fragment
        }
        setupOption(binding.optionSecurity, R.drawable.ic_security, "Contraseña y Seguridad") {
            Toast.makeText(requireContext(), "Navegar a Contraseña y Seguridad", Toast.LENGTH_SHORT).show()
        }
        // Nota: El diseño tenía "Contanensa y Seguridad" repetido, asumo que era un typo y lo reemplazo
        setupOption(binding.optionPaymentMethod, R.drawable.ic_payment_method, "Método de Pago") {
            Toast.makeText(requireContext(), "Navegar a Método de Pago", Toast.LENGTH_SHORT).show()
        }
        setupOption(binding.optionLegalPrivacy, R.drawable.ic_legal_privacy, "Legales y Privacidad") {
            Toast.makeText(requireContext(), "Navegar a Legales y Privacidad", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupOptions() {
        // En tu diseño, tienes un include para cada opción.
        // `item_account_option.xml` tiene un View para el separador.
        // Aquí ajustamos la visibilidad del separador para la última opción.
        binding.optionLegalPrivacy.divider.visibility = View.GONE
    }

    private fun setupOption(
        optionBinding: ItemAccountOptionBinding,
        iconResId: Int,
        title: String,
        action: () -> Unit
    ) {
        optionBinding.ivOptionIcon.setImageResource(iconResId)
        optionBinding.tvOptionTitle.text = title
        optionBinding.root.setOnClickListener { action() }
    }


    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Cerrar Sesión", Toast.LENGTH_SHORT).show()
            // Lógica para cerrar sesión: limpiar preferencias, navegar a pantalla de login, etc.
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}